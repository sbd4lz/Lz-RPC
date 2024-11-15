package com.liangzai.lzrpc.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author dengpei
 * @Date 2024/10/29 17:30
 * @Descprition
 */
@Slf4j
public class VertxHttpServer implements Server {
	@Override
	public void doStart(int port) {
		// 创建Vert.x实例
		Vertx vertx = Vertx.vertx();

		// 创建HTTP服务器
		io.vertx.core.http.HttpServer server = vertx.createHttpServer();

		// 监听端口并处理请求
//		server.requestHandler(request -> {
//			System.out.println("Received request: " + request.method() + " " + request.uri());
//			request.response()
//					.putHeader("content-type", "text/plain")
//					.end("Hello from Vert.x HTTP server!");
//		});

		server.requestHandler(new VertxHttpServerHandler());

		// 启动HTTP服务器并监听指定端口
		server.listen(port, result -> {
			if (result.succeeded()) {
				log.info("Server is now listening on port " + port);
			} else {
				log.error("Failed to start server: " + result.cause());
			}
		});

	}
}
