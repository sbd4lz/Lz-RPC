package com.liangzai.lzrpc.fault.tolerant;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:44
 * @Descprition
 */
public class TolerantStrategyKeys {

	/**
	 * 故障恢复
	 */
	public static final String FAIL_BACK= "failBack";

	/**
	 * 快速失败
	 */
	public static final String FAIL_FAST= "failFast";

	/**
	 * 故障转移
	 */
	public static final String FAIL_OVER= "failOver";

	/**
	 * 静默处理
	 */
	public static final String FAIL_SAFE= "failSafe";

}
