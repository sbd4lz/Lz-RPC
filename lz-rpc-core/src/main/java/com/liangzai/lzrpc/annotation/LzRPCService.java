package com.liangzai.lzrpc.annotation;

import com.liangzai.lzrpc.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author dengpei
 * @Date 2024/11/11 17:28
 * @Descprition 服务提供者注解（用于注册服务）
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LzRPCService {

	/**
	 * 服务接口类
	 */
	Class<?> interfaceClass() default void.class;

	/**
	 * 版本
	 */
	String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
