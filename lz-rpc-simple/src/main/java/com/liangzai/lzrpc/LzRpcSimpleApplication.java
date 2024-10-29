package com.liangzai.lzrpc;

import com.liangzai.lzrpc.server.HttpServer;
import com.liangzai.lzrpc.server.VertxHttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LzRpcSimpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LzRpcSimpleApplication.class, args);

		HttpServer vertxHttpServer = new VertxHttpServer();
		vertxHttpServer.doStart(8080);
	}

}
