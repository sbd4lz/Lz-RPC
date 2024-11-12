package com.liangzai.lzrpc.proxy;

import com.liangzai.lzrpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @Author dengpei
 * @Date 2024/10/31 10:53
 * @Descprition 服务代理工厂
 */
public class ServiceProxyFactory {
	public static <T> T getProxy(Class<T> serviceClass){
		if(RpcApplication.getRpcConfig().isMock()){
			return getMockServiceProxy(serviceClass);
		}
		return getTcpServiceProxy(serviceClass);
	}

	/**
	 * 根据服务类获取 Mock 代理对象
	 *
	 * @param serviceClass
	 * @param <T>
	 * @return
	 */
	public static <T> T getMockServiceProxy(Class<T> serviceClass){
		return (T) Proxy.newProxyInstance(
				serviceClass.getClassLoader(),
				new Class[]{serviceClass},
				new MockServiceProxy());
	}
	/**
	 * 根据服务类获取代理对象
	 * Proxy.newProxyInstance 方法用于创建一个动态代理实例，它有三个参数，每个参数的作用如下：
	 * 1. loader (ClassLoader)  类型：ClassLoader
	 *   用于定义代理类的类加载器。这个类加载器负责加载代理类的字节码。
	 * 2. interfaces (Class[])  类型：Class[]
	 *   一个 Class 类型的数组，表示代理类需要实现的接口。动态代理只能为接口创建实例，因此该数组不能包含实现类。
	 * 3. h (InvocationHandler)  类型：InvocationHandler
	 *   一个接口，包含一个方法 invoke(Object proxy, Method method, Object[] args)。当代理类的方法被调用时，这个方法会被执行。
	 *   proxy 是被代理的实例，method 是被调用的方法，args 是方法传入的参数。
	 */
	public static <T> T getHttpServiceProxy(Class<T> serviceClass){
		return (T) Proxy.newProxyInstance(
				serviceClass.getClassLoader(),
				new Class[]{serviceClass},
				new HttpServiceProxy());
	}

	public static <T> T getTcpServiceProxy(Class<T> serviceClass){
		return (T) Proxy.newProxyInstance(
				serviceClass.getClassLoader(),
				new Class[]{serviceClass},
				new TcpServiceProxy());
	}
}
