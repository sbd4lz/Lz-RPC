package com.liangzai.lzrpc.bootstrap;

import com.liangzai.lzrpc.annotation.LzRPCReference;
import com.liangzai.lzrpc.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @Author dengpei
 * @Date 2024/11/11 19:42
 * @Descprition LzRPCReference注解处理类
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {

	/**
	 * Bean 初始化后执行，注册服务
	 *
	 * @param bean 初始化后的Bean实例
	 * @param beanName bean实例名称
	 * @return 处理后的bean实例
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> beanClass = bean.getClass();
		// 遍历对象的所有属性
		Field[] declaredFields = beanClass.getDeclaredFields();
		for(Field field : declaredFields){
			// 逐一判断属性是否存在@RpcReference注解
			LzRPCReference rpcReference = field.getAnnotation(LzRPCReference.class);
			if(rpcReference != null){
				// 获取该属性的接口
				Class<?> interfaceClass = rpcReference.interfaceClass();
				if (interfaceClass == void.class) {
					interfaceClass = field.getType();
				}
				field.setAccessible(true);
				Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
				try {
					// 将该属性设为代理类
					field.set(bean, proxyObject);
					field.setAccessible(false);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("为字段注入代理对象失败", e);
				}
			}
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
