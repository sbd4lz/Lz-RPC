package com.liangzai.lzrpc;

import com.liangzai.lzrpc.fault.retry.FixedIntervalRetryStrategy;
import com.liangzai.lzrpc.fault.retry.RetryStrategy;
import com.liangzai.lzrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @Author dengpei
 * @Date 2024/11/10 11:19
 * @Descprition
 */
@Slf4j
public class RetryTests {

	RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

	@Test
	public void doRetry() throws InterruptedException {
		try {
			RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
				System.out.println("测试重试");
				throw new RuntimeException("模拟重试失败");
			});
			System.out.println(rpcResponse);
		} catch (Exception e) {
			System.out.println("重试多次失败");
			e.printStackTrace();
		}
		Thread.sleep(10000);
	}
}
