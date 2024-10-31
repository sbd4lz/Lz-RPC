package com.liangzai.example.provider;

import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.server.HttpServer;
import com.liangzai.lzrpc.server.HutoolHttpServer;
import com.liangzai.lzrpc.server.VertxHttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);
		// RPC框架初始化
		RpcApplication.init();
		// 注册服务 todo 用注解实现，启动时自动到注册中心注册服务，而不是注册到本地
		LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
//		// 启动web服务 todo 该服务应该由rpc-core启动
//		HttpServer hutoolServer = new HutoolHttpServer();
//		hutoolServer.doStart(RpcApplication.getRpcConfig().getServerPort());
		HttpServer vertxHttpServer = new VertxHttpServer();
		vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
	}

}
