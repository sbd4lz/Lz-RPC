package com.liangzai.example.provider;

import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.server.HttpServer;
import com.liangzai.lzrpc.server.VertxHttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);
		// RPC框架初始化
		RpcApplication.init();
		// 注册服务
		LocalRegistry.register(UserServiceImpl.class.getName(), UserServiceImpl.class);
		// 启动web服务
		HttpServer vertxHttpServer = new VertxHttpServer();
		vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
	}

}
