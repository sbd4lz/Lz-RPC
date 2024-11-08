package com.liangzai.lzrpc.loadbalancer;

import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;
import java.util.Random;

/**
 * @Author dengpei
 * @Date 2024/11/8 18:58
 * @Descprition 随机负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer{

	private final Random random = new Random();
	@Override
	public ServiceMetaInfo select(Map<String, Object> requestParams, Map<String, ServiceMetaInfo> serviceMetaInfoMap) {
		int serviceNodeNum = serviceMetaInfoMap.size();
		if(serviceNodeNum == 1){
			return serviceMetaInfoMap.values().iterator().next();
		}
		// 由于Map无序，引入一个计数器作为索引
		int index = random.nextInt(serviceNodeNum);
		int counter = 0;
		for (String serviceNodeKey: serviceMetaInfoMap.keySet()){
			if(index == counter){
				return serviceMetaInfoMap.get(serviceNodeKey);
			}
			counter ++;
		}
		return null;
	}
}
