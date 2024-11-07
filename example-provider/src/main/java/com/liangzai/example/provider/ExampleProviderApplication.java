package com.liangzai.example.provider;

import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.server.VertxServer;
import com.liangzai.lzrpc.server.tcp.VertxTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);
		// RPC框架初始化
		RpcApplication.init();

		// 注册服务到本地
		String serviceName = UserService.class.getName();
		LocalRegistry.register(serviceName, UserServiceImpl.class);
		// 注册服务地址到注册中心
		RpcConfig rpcConfig = RpcApplication.getRpcConfig();
		RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
		Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
		ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
		serviceMetaInfo.setServiceName(serviceName);
		serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
		serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
		try {
			registry.register(serviceMetaInfo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

//		// 启动web服务 todo 该服务应该由rpc-core启动
//		HttpServer hutoolServer = new HutoolHttpServer();
//		hutoolServer.doStart(RpcApplication.getRpcConfig().getServerPort());
		VertxServer vertxTcpServer = new VertxTcpServer();
		vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
	}

}
