package com.liangzai.lzrpc.bootstrap;

import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.model.ServiceRegisterInfo;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @Author dengpei
 * @Date 2024/11/11 16:35
 * @Descprition 服务提供者初始化
 */
public class ProviderBootstrap {
	public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList){
		// RPC 框架初始化（配置和注册中心）
		RpcApplication.init();
		// 全局配置
		final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

		// 注册服务
		for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList){
			String serviceName = serviceRegisterInfo.getServiceName();
			// 本地注册
			LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
			// 注册到注册中心
			RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
			Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(serviceName);
			serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
			serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
			try {
				registry.register(serviceMetaInfo);
			} catch (Exception e) {
				throw new RuntimeException(serviceName + " 服务注册失败", e);
			}
		}

		// 启动服务器
		VertxTcpServer vertxTcpServer = new VertxTcpServer();
		vertxTcpServer.doStart(rpcConfig.getServerPort());
	}
}
