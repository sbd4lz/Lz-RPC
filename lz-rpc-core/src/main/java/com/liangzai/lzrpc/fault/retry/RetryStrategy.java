package com.liangzai.lzrpc.fault.retry;

import com.liangzai.lzrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author Kitsch
 */
public interface RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}