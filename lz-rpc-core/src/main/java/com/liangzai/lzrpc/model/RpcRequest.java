package com.liangzai.lzrpc.model;

import com.liangzai.lzrpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author dengpei
 * @Date 2024/10/29 19:36
 * @Descprition
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
	/**
	 * 服务名称
	 */
	private String serviceName;

	/**
	 * 方法名称
	 */
	private String methodName;

	/**
	 * 参数类型列表
	 */
	private Class<?>[] parameterTypes;

	/**
	 * 参数列表
	 */
	private Object[] args;

	/**
	 * 服务版本
	 */
	private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
}
