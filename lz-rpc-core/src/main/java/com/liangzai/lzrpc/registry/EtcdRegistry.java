package com.liangzai.lzrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Kitsch
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();
    
    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public boolean register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        return localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        DeleteResponse deleteResponse = kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public Map<String, ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException {
        // 优先从缓存获取服务
        Map<String, ServiceMetaInfo> cachedServiceMetaInfoMap = registryServiceCache.readCache(serviceKey);
        if (cachedServiceMetaInfoMap != null) {
            return cachedServiceMetaInfoMap;
        }
        // 前缀搜索
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;
        try {
            // 监听前缀为serviceKey的键的变化(若还未进行监听)
            if(!registryServiceCache.containsServiceKey(serviceKey)){
                watch(serviceKey);
            }
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            keyValues.forEach(keyValue -> {
                String serviceNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(keyValue.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class);
                registryServiceCache.writeCache(serviceKey, serviceNodeKey, serviceMetaInfo);
            });
            // 写入服务缓存
            return registryServiceCache.readCache(serviceKey);
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void heartBeat() {
        // 使用 Hutool 工具类的 CronUtil 实现定时任务，对所有集合中的节点执行 重新注册 操作
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceKey);
        if(newWatch){
            // 监听前缀为serviceKey的所有serviceNodeKey
            WatchOption watchOption = WatchOption.builder().isPrefix(true).build();
            String watchKey = ETCD_ROOT_PATH + serviceKey;
            watchClient.watch(ByteSequence.from(watchKey, StandardCharsets.UTF_8), watchOption, response -> {
                for(WatchEvent event : response.getEvents()){
                    // 获取发生变化的serviceNodeKey
                    KeyValue keyValue = event.getKeyValue();
                    String watchServiceNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                    switch(event.getEventType()){
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache(serviceKey, watchServiceNodeKey);
                            break;
                        case PUT:
                            // 判断是否属于心跳续租
                            if(registryServiceCache.containsCache(serviceKey, watchServiceNodeKey)){
                                break;
                            }
                            ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(keyValue.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class);
                            registryServiceCache.writeCache(serviceKey, watchServiceNodeKey, serviceMetaInfo);
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        // 下线节点
        // 遍历本节点所有的 key
        for (String key : localRegisterNodeKeySet) {
            try {
                log.info("服务节点下线：" + key);
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException("节点下线失败：" + key);
            }
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

}
