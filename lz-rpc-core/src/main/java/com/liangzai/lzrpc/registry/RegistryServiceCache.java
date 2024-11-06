package com.liangzai.lzrpc.registry;

import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author dengpei
 * @Date 2024/11/5 13:05
 * @Descprition
 */
public class RegistryServiceCache {

	/**
	 * 服务缓存 serviceKey ->  serviceKeyNode -> ServiceMetaInfo
	 */
	Map<String, Map<String, ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

	/**
	 * 写缓存
	 *
	 * @param newServiceCache
	 * @return
	 */
	void writeCache(String serviceKey, String serviceNodeKey, ServiceMetaInfo newServiceCache) {
		serviceCache.compute(serviceKey, (k, innerMap) -> {
			if(innerMap == null){
				innerMap = new HashMap<>();
			}
			innerMap.put(serviceNodeKey, newServiceCache);
			return innerMap;
		});
	}

	/**
	 *  写缓存
	 * @param serviceKey
	 * @param newServiceCacheMap
	 */
	void writeCache(String serviceKey, Map<String, ServiceMetaInfo> newServiceCacheMap) {
		serviceCache.put(serviceKey, newServiceCacheMap);
	}

	Map<String, ServiceMetaInfo> readCache(String serviceKey){
		return serviceCache.get(serviceKey);
	}

	/**
	 * 清除serviceKey缓存
	 */
	void clearCache(String serviceKey) {
		serviceCache.remove(serviceKey);
	}

	/**
	 * 清除serviceKey 中的 serviceNodeKey
	 */
	void clearCache(String serviceKey, String serviceNodeKey) {
		serviceCache.computeIfPresent(serviceKey, (k, innerMap) -> {
			innerMap.remove(serviceNodeKey);
			return innerMap;
		});
	}


	/**
	 * 判断缓存中是否包含某个服务的任意结点
	 * @param serviceKey
	 * @return
	 */
	boolean containsServiceKey(String serviceKey) {
		return serviceCache.containsKey(serviceKey);
	}

	public boolean containsCache(String serviceKey, String serviceNodeKey) {
		if(!containsServiceKey(serviceKey)){
			return false;
		}
		Map<String, ServiceMetaInfo> serviceInfoMap = serviceCache.get(serviceKey);
		return serviceInfoMap.containsKey(serviceNodeKey);
	}
}
