package com.liangzai.lzrpc.server;

/**
 * @Author dengpei
 * @Date 2024/10/29 17:30
 * @Descprition
 */
public interface HttpServer {

	/**
	 * 启动服务器
	 *
	 * @param port
	 */
	void doStart(int port);
}
