package com.liangzai.lzrpc.registry;

import com.liangzai.lzrpc.serializer.SpiLoader;

/**
 * @Author dengpei
 * @Date 2024/11/3 14:44
 * @Descprition
 */
public class RegistryFactory {

	static{
		SpiLoader.load(Registry.class);
	}

	/**
	 * 默认注册中心
	 */
	private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

	/**
	 * 获取实例
	 * @param key
	 * @return
	 */
	public static Registry getInstance(String key) {
		return SpiLoader.getInstance(Registry.class, key);
	}

}
