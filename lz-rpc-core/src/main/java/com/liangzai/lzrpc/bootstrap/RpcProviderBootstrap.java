package com.liangzai.lzrpc.bootstrap;

import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.annotation.LzRPCService;
import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @Author dengpei
 * @Date 2024/11/11 19:17
 * @Descprition LzRPCService注解处理类
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {


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
		// 根据bean上的注解获取服务注册的参数
		LzRPCService rpcService = beanClass.getAnnotation(LzRPCService.class);
		if(rpcService != null){
			// 获取注解内标注的接口类，若未指定，则获取注解应用对象的接口类
			Class<?> interfaceClass = rpcService.interfaceClass();
			if (interfaceClass == void.class) {
				interfaceClass = beanClass.getInterfaces()[0];
			}
			String serviceName = interfaceClass.getName();
			String serviceVersion = rpcService.serviceVersion();

			// 注册到本地
			LocalRegistry.register(serviceName, beanClass);
			// 全局配置
			final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
			// 注册服务到注册中心
			RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
			Registry registry = RegistryFactory.getInstance(registryConfig.getType());
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(serviceName);
			serviceMetaInfo.setServiceVersion(serviceVersion);
			serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
			serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
			try {
				registry.register(serviceMetaInfo);
				log.info(serviceName + " 服务注册成功");
			} catch (Exception e) {
				throw new RuntimeException(serviceName + " 服务注册失败", e);
			}
		}
		return bean;
	}

}
