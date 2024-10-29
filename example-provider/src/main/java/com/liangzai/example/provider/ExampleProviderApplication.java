package com.liangzai.example.provider;

import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.server.HttpServer;
import com.liangzai.lzrpc.server.VertxHttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);

		LocalRegistry.register(UserServiceImpl.class.getName(), UserServiceImpl.class);
		HttpServer vertxHttpServer = new VertxHttpServer();
		vertxHttpServer.doStart(8080);
	}

}
