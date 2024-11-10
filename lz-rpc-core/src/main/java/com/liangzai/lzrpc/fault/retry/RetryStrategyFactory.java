package com.liangzai.lzrpc.fault.retry;

import com.liangzai.lzrpc.config.SpiLoader;

/**
 * @Author dengpei
 * @Date 2024/11/10 11:30
 * @Descprition
 */
public class RetryStrategyFactory {
	static {
		SpiLoader.load(RetryStrategy.class);
	}

	public static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

	/**
	 * 获取实例
	 *
	 * @param key
	 * @return
	 */
	public static RetryStrategy getInstance(String key) {
		return SpiLoader.getInstance(RetryStrategy.class, key);
	}

}
