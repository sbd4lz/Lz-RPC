package com.liangzai.example.consumer;

import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RpcConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class ExampleConsumerApplicationTests {

	@Test
	void contextLoads() {
		RpcConfig rpcConfig = RpcApplication.getRpcConfig();
		System.out.println(rpcConfig.toString());
	}

}
