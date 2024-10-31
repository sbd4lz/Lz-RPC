package com.liangzai.lzrpc.server;

import cn.hutool.http.HttpUtil;

/**
 * @Author dengpei
 * @Date 2024/10/31 17:15
 * @Descprition
 */
public class HutoolHttpServer implements HttpServer{
	@Override
	public void doStart(int port) {
		HttpUtil.createServer(port)
				.addAction("/hello", (req, res)->{
					res.write("Hello Hutool Server");
				})
				.addHandler("/", new HutoolHttpServerHandler())
				.start();
	}
}
