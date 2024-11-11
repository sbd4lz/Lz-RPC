package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.config.SpiLoader;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:46
 * @Descprition 容错策略工厂类
 */
public class TolerantStrategyFactory {
	static {
		SpiLoader.load(TolerantStrategy.class);
	}

	public static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailSafeTolerantStrategy();

	public static TolerantStrategy getInstance(String key){
		return SpiLoader.getInstance(TolerantStrategy.class, key);
	}
}
