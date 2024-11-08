package com.liangzai.lzrpc.loadbalancer;

import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author dengpei
 * @Date 2024/11/8 18:30
 * @Descprition 轮询负载均衡器
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

	private final AtomicInteger currentIndex = new AtomicInteger(0);

	@Override
	public ServiceMetaInfo select(Map<String, Object> requestParams, Map<String, ServiceMetaInfo> serviceMetaInfoMap) {
		int serviceNodeNum = serviceMetaInfoMap.size();
		if(serviceNodeNum == 1){
			return serviceMetaInfoMap.values().iterator().next();
		}
		// 由于Map无序，引入一个计数器作为索引
		ServiceMetaInfo serviceMetaInfo = null;
		int index = currentIndex.getAndIncrement();
		int counter = 0;
		for (String serviceNodeKey: serviceMetaInfoMap.keySet()){
			if(index % serviceNodeNum == counter){
				serviceMetaInfo = serviceMetaInfoMap.get(serviceNodeKey);
			}
			counter = (counter + 1) % serviceNodeNum;
		}

		return serviceMetaInfo;
	}
}
