package com.liangzai.lzrpc;

import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.server.HttpServer;
import com.liangzai.lzrpc.server.HutoolHttpServer;

/**
 * @author Kitsch
 */
//@SpringBootApplication
public class LzRpcCoreApplication {

	public static void main(String[] args) {
//		SpringApplication.run(LzRpcCoreApplication.class, args);

		RpcConfig rpc = RpcApplication.getRpcConfig();
//		HttpServer vertxHttpServer = new VertxHttpServer();
//		vertxHttpServer.doStart(rpc.getServerPort());
		HttpServer hutoolServer = new HutoolHttpServer();
		hutoolServer.doStart(rpc.getServerPort());
	}

}
