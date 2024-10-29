package com.liangzai.lzrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author dengpei
 * @Date 2024/10/29 17:54
 * @Descprition
 */
public class LocalRegistry {

	/**
	 * 服务注册 key-服务名，value-服务的实现类
	 */
	private static final Map<String, Class<?>> serviceMap = new ConcurrentHashMap<>();

	/**
	 * 注册服务
	 * @param serviceName 服务名称
	 * @param implClass 实现类
	 */
	public static void register(String serviceName, Class<?> implClass){
		serviceMap.put(serviceName, implClass);
	}

	/**
	 * 获取服务
	 * @param serviceName 服务名称
	 * @return 实现类
	 */
	public static Class<?> get(String serviceName){
		return serviceMap.get(serviceName);
	}

	/**
	 * 取消服务
	 * @param serviceName 服务名称
	 */
	public static void remove(String serviceName){
		serviceMap.remove(serviceName);
	}


}
