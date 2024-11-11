package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.model.RpcResponse;

import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:42
 * @Descprition 转移到其它服务结点
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
	@Override
	public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
		// todo 获取其它服务结点并调用 可以利用容错方法的上下文参数传递所有的服务节点和本次调用的服务节点，选择一个其他节点再次发起调用。
		return null;
	}
}
