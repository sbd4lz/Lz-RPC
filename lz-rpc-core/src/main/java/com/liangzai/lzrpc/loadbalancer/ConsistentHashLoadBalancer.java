package com.liangzai.lzrpc.loadbalancer;

import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;
import java.util.TreeMap;

/**
 * @Author dengpei
 * @Date 2024/11/8 19:03
 * @Descprition 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

	/**
	 * 一致性 Hash 环，存放虚拟节点
	 */
	TreeMap<Integer, String> hashCircle = new TreeMap<>();

	/**
	 * 虚拟节点数
	 */
	private static final int VIRTUAL_NODE_NUM = 100;
	@Override
	public synchronized ServiceMetaInfo select(Map<String, Object> requestParams, Map<String, ServiceMetaInfo> serviceMetaInfoMap) {
		int serviceNodeNum = serviceMetaInfoMap.size();
		if(serviceNodeNum == 1){
			return serviceMetaInfoMap.values().iterator().next();
		}
		// 构建虚拟结点环
		for(String serviceNodeKey : serviceMetaInfoMap.keySet()){
			for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
				// 每个结点映射出若干个虚拟节点
				int hash = getHash(serviceNodeKey + "#" + i);
				hashCircle.put(hash, serviceNodeKey);
			}
		}
		int hash = getHash(requestParams);
		// ceilingEntry 获取大于等于指定参数的键值对
		Map.Entry<Integer, String> entry = hashCircle.ceilingEntry(hash);
		if (entry == null) {
			// 如果没有大于等于调用请求 hash 值的虚拟节点，则返回环首部的节点
			entry = hashCircle.firstEntry();
		}
		return serviceMetaInfoMap.get(entry.getValue());
	}

	/**
	 * Hash 算法
	 *
	 * @param key
	 * @return
	 */
	private int getHash(Object key) {
		return key.hashCode();
	}
}
