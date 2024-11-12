package com.liangzai.lzrpc.fault;

import com.liangzai.lzrpc.model.RpcResponse;

/**
 * @Author dengpei
 * @Date 2024/11/12 15:07
 * @Descprition
 */
public class FailBackService {

	public static RpcResponse mock(){
		return new RpcResponse();
	}
}
