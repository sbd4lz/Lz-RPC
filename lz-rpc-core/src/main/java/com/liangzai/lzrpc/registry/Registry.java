package com.liangzai.lzrpc.registry;

import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Kitsch
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（注册单个节点，服务端）
     *
     * @param serviceMetaInfo
     * @return
     */
    boolean register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务（服务端）
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     *
     * @param serviceKey 服务键名
     * @return
     */
    Map<String, ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException;

    /**
     * 心跳检测（服务端）
     */
    void heartBeat();

    /**
     * 监听（消费端）
     *
     * @param serviceKey
     */
    void watch(String serviceKey);

    /**
     * 服务销毁（服务端）
     */
    void destroy();


}