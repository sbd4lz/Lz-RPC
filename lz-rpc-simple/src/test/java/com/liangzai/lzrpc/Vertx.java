package com.liangzai.lzrpc;

import org.junit.jupiter.api.Test;

/**
 * @Author dengpei
 * @Date 2024/10/29 17:51
 * @Descprition
 */
public class Vertx {

	@Test
	void test1() throws InterruptedException {
		int port = 8080;
		io.vertx.core.Vertx vertx = io.vertx.core.Vertx.vertx();

		// 创建HTTP服务器
		io.vertx.core.http.HttpServer server = vertx.createHttpServer();

		// 监听端口并处理请求
		server.requestHandler(request -> {
			System.out.println("Received request: " + request.method() + " " + request.uri());
			request.response()
					.putHeader("content-type", "text/plain")
					.end("Hello from Vert.x HTTP server!");
		});

//		server.requestHandler(new HttpServerHandler());

		// 启动HTTP服务器并监听指定端口
		server.listen(port, result -> {
			if (result.succeeded()) {
				System.out.println("Server is now listening on port " + port);
			} else {
				System.err.println("Failed to start server: " + result.cause());
			}
		});
	}
}
