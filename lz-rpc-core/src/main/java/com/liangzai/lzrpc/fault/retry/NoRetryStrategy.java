package com.liangzai.lzrpc.fault.retry;

import com.liangzai.lzrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @Author dengpei
 * @Date 2024/11/10 11:10
 * @Descprition 不重试
 */
public class NoRetryStrategy implements RetryStrategy{
	@Override
	public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
		return callable.call();
	}
}
