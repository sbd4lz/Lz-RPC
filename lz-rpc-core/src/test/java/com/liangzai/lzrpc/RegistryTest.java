package com.liangzai.lzrpc;

import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.EtcdRegistry;
import com.liangzai.lzrpc.registry.Registry;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost:xxxxxx");
        serviceMetaInfo.setServicePort(543435);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() throws ExecutionException, InterruptedException {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() throws Exception {
        init();
        register();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        Map<String, ServiceMetaInfo> serviceMetaInfoMap = registry.serviceDiscovery(serviceKey);
        System.out.println(serviceMetaInfoMap + "\n1111111111111");
        serviceMetaInfoMap = registry.serviceDiscovery(serviceKey);
        System.out.println(serviceMetaInfoMap + "\n2222222222222");
        unRegister();
        serviceMetaInfoMap = registry.serviceDiscovery(serviceKey);
        System.out.println(serviceMetaInfoMap + "\n3333333333333");
        register();
        serviceMetaInfoMap = registry.serviceDiscovery(serviceKey);
        System.out.println(serviceMetaInfoMap + "\n4444444444444");
        Assert.assertNotNull(serviceMetaInfoMap);
    }

    @Test
    public void heartBeat() throws Exception{
        register();
        Thread.sleep(60 * 1000L);
    }
}