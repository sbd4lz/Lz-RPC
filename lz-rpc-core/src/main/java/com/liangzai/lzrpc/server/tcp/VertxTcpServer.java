package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.server.VertxServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
/**
 * @Author dengpei
 * @Date 2024/11/7 15:41
 * @Descprition
 */
public class VertxTcpServer implements VertxServer {

	private byte[] handleRequest(byte[] requestData) {
		// 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
		// 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
		return "Hello, client!".getBytes();
	}
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
	public static void main(String[] args) {
		new VertxTcpServer().doStart(8888);
	}
}
