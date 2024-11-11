package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.model.RpcResponse;

import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:33
 * @Descprition 快速失败
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
	@Override
	public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
		throw new RuntimeException("服务错误", e);
	}
}
