package com.liangzai.lzrpc.config;

import cn.hutool.core.io.resource.ResourceUtil;
import com.liangzai.lzrpc.fault.retry.RetryStrategy;
import com.liangzai.lzrpc.fault.tolerant.TolerantStrategy;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器（支持键值对映射）
 * @author Kitsch
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名 =>（key => 实现类）
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class, Registry.class, RetryStrategy.class, TolerantStrategy.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例
     *
     * @param tClass
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        /** note ConcurrentMap的复合操作不能保证原子性，多线程情况下可能导致结果不符合预期：
         * if (!map.containsKey(key)) {
         *     map.put(key, value);
         * }
         * 使用 ConcurrentMap 提供的原子性操作
         */
        String implClassName = implClass.getName();
//        if (!instanceCache.containsKey(implClassName))  不要这么写，看上面的note
//            instanceCache.put(implClassName, implClass.newInstance());
            try {
                instanceCache.putIfAbsent(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }

        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型(基于Hutool ResourceUtil实现)
     *
     * @param loadClass 所要加载的类
     * @throws IOException
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 扫描路径，用户自定义的 SPI 优先级高于系统 SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try(InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    // note 可以只调用 BufferedReader 的 close() 方法，因为 BufferedReader 在其 close() 方法中会自动关闭它所包装的 InputStreamReader
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                            log.info("加载{}: {}", key, className);
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 加载某个类型(基于SPI ServiceLoader实现)
     *
     * @param loadClass 所要加载的类
     * @throws IOException
     */
    public static Map<String, Class<?>> serviceLoad(Class<?> loadClass){
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        // 该类固定读取META-INF/services目录下的文件
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        for(Serializer load : loader){
            keyClassMap.put(load.getClass().getName(), load.getClass());
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

}
