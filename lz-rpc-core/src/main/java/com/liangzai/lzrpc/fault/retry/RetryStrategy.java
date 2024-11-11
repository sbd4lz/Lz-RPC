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
    // todo 实现指数退避算法重试和随机时间重试
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}