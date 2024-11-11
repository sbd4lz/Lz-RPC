package com.liangzai.lzrpc.bootstrap;

import com.liangzai.lzrpc.RpcApplication;

/**
 * @Author dengpei
 * @Date 2024/11/11 16:54
 * @Descprition 服务消费者初始化
 */
public class ConsumerBootstrap {

	/**
	 * 初始化
	 */
	public static void init() {
		// RPC 框架初始化（配置和注册中心）
		RpcApplication.init();
	}
}
