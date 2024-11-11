package com.liangzai.lzrpc.fault.tolerant;

import com.liangzai.lzrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/11 15:39
 * @Descprition 静默处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
	@Override
	public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
		log.info("服务错误, 静默处理", e);
		return new RpcResponse();
	}
}
