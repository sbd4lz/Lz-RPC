package com.liangzai.lzrpc;

import com.liangzai.lzrpc.loadbalancer.LoadBalancer;
import com.liangzai.lzrpc.loadbalancer.RandomLoadBalancer;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 负载均衡器测试
 */
public class LoadBalancerTest {

    final LoadBalancer loadBalancer = new RandomLoadBalancer();

    @Test
    public void select() {
        // 请求参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "apple2");
        // 服务列表
        ServiceMetaInfo serviceMetaInfo1 = new ServiceMetaInfo();
        serviceMetaInfo1.setServiceName("myService");
        serviceMetaInfo1.setServiceVersion("1.0");
        serviceMetaInfo1.setServiceHost("localhost");
        serviceMetaInfo1.setServicePort(1234);
        ServiceMetaInfo serviceMetaInfo2 = new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("myService");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("yupi.icu");
        serviceMetaInfo2.setServicePort(80);
        ServiceMetaInfo serviceMetaInfo3 = new ServiceMetaInfo();
        serviceMetaInfo3.setServiceName("myService");
        serviceMetaInfo3.setServiceVersion("1.0");
        serviceMetaInfo3.setServiceHost("y43245433");
        serviceMetaInfo3.setServicePort(3214325);

        Map<String, ServiceMetaInfo> serviceMetaInfoMap = new HashMap<>();
        serviceMetaInfoMap.put(serviceMetaInfo1.getServiceNodeKey(), serviceMetaInfo1);
        serviceMetaInfoMap.put(serviceMetaInfo2.getServiceNodeKey(), serviceMetaInfo2);
        serviceMetaInfoMap.put(serviceMetaInfo3.getServiceNodeKey(), serviceMetaInfo3);
        // 连续调用 3 次
        ServiceMetaInfo serviceMetaInfo;
        for (int i = 0; i < 10; i++) {
            serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoMap);
            System.out.println(serviceMetaInfo);
            Assert.assertNotNull(serviceMetaInfo);
        }
    }
}