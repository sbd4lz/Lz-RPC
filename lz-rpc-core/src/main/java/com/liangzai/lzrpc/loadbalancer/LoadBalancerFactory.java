package com.liangzai.lzrpc.loadbalancer;

import com.liangzai.lzrpc.config.SpiLoader;

/**
 * @Author dengpei
 * @Date 2024/11/8 20:02
 * @Descprition 负载均衡器工厂
 */
public class LoadBalancerFactory {

	static {
		SpiLoader.load(LoadBalancer.class);
	}

	/**
	 * 默认负载均衡器
	 */
	private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

	/**
	 * 获取实例
	 *
	 * @param key
	 * @return
	 */
	public static LoadBalancer getInstance(String key) {
		return SpiLoader.getInstance(LoadBalancer.class, key);
	}

}