package com.liangzai.lzrpc.model;

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
public class RpcResponse implements Serializable {
	/**
	 * 响应数据
	 */
	private Object data;

	/**
	 * 响应数据类型（预留）
	 */
	private Class<?> dataType;

	/**
	 * 响应信息
	 */
	private String message;

	/**
	 * 异常信息
	 */
	private Exception exception;
}
