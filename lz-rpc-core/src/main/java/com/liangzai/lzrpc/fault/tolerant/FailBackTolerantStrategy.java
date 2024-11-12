package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.fault.FailBackService;
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

		return FailBackService.mock();
	}
}
