package com.liangzai.lzrpc.fault.retry;

import com.github.rholder.retry.*;
import com.liangzai.lzrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Author dengpei
 * @Date 2024/11/12 11:11
 * @Descprition 斐波那契间隔重试
 */
@Slf4j
public class FibonacciIntervalRetryStrategy implements RetryStrategy{
	@Override
	public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
		Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
				.retryIfExceptionOfType(Exception.class)
				.withWaitStrategy(WaitStrategies.fibonacciWait(15, TimeUnit.SECONDS))
				.withStopStrategy(StopStrategies.stopAfterAttempt(3))
				.withRetryListener(new RetryListener() {
					@Override
					public <V> void onRetry(Attempt<V> attempt) {
						log.info("重试次数{}", attempt.getAttemptNumber());
					}
				})
				.build();
		return null;
	}
}
