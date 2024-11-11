package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.model.RpcResponse;

import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:41
 * @Descprition 服务降级
 */
public class FailBackTolerantStrategy implements TolerantStrategy{
	@Override
	public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
		// todo 编写本地降级服务 可以参考 Dubbo 的 Mock 能力，让消费端指定调用失败后要执行的本地服务和方法。
		return null;
	}
}
