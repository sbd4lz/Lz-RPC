package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
/**
 * @Author dengpei
 * @Date 2024/11/7 15:41
 * @Descprition
 */
public class VertxTcpServer implements Server {

	@Override
	public void doStart(int port) {
		// 创建 Vert.x 实例
		Vertx vertx = Vertx.vertx();

		// 创建 TCP服务器
		NetServer server = vertx.createNetServer();

		server.connectHandler(new VertxTcpServerHandler());

		// 启动 TCP 服务器并监听指定端口
		server.listen(port, result -> {
			if (result.succeeded()) {
				System.out.println("TCP server started on port " + port);
			} else {
				System.err.println("Failed to start TCP server: " + result.cause());
			}
		});

	}

}
