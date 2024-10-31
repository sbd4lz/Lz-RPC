package com.liangzai.lzrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @Author dengpei
 * @Date 2024/10/31 10:53
 * @Descprition 服务代理工厂
 */
public class ServiceProxyFactory {


	/**
	 * 根据服务类获取代理对象
	 * @param serviceClass 服务类名称
	 * @return 代理对象
	 * @param <T>
	 */
	public static <T> T getProxy(Class<T> serviceClass){
		return (T) Proxy.newProxyInstance(
				serviceClass.getClassLoader(),
				new Class[]{serviceClass},
				new ServiceProxy());

	}
}
