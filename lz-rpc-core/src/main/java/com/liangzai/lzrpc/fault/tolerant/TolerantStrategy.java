package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.model.RpcResponse;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:31
 * @Descprition 容错策略
 */
public interface TolerantStrategy {

	/**
	 * 容错策略有很多种，常用的容错策略主要是以下几个：
	 * 1）Fail-Over 故障转移：一次调用失败后，切换一个其他节点再次进行调用，也算是一种重试。
	 * 2）Fail-Back 失败自动恢复：系统的某个功能出现调用失败或错误时，通过其他的方法，恢复该功能的正常。可以理解为降级，比如重试、调用其他服务等。
	 * 3）Fail-Safe 静默处理：系统出现部分非重要功能的异常时，直接忽略掉，不做任何处理，就像错误没有发生过一样。
	 * 4）Fail-Fast 快速失败：系统出现调用错误时，立刻报错，交给外层调用方处理。
	 */

	/**
	 * 容错
	 *
	 * @param context 上下文，用于传递数据
	 * @param e       异常
	 * @return
	 */
	RpcResponse doTolerant(Map<String, Object> context, Exception e);

}
