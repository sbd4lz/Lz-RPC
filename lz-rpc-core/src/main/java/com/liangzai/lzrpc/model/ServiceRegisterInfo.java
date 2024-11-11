package com.liangzai.lzrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dengpei
 * @Date 2024/11/11 16:34
 * @Descprition 服务注册信息类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo <T>{

	/**
	 * 服务名称
	 */
	private String serviceName;

	/**
	 * 实现类
	 */
	private Class<? extends T> implClass;
}
