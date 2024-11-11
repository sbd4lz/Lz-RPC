package com.liangzai.lzrpc.annotation;

import com.liangzai.lzrpc.bootstrap.RpcConsumerBootstrap;
import com.liangzai.lzrpc.bootstrap.RpcInitBootstrap;
import com.liangzai.lzrpc.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author dengpei
 * @Date 2024/11/11 17:25
 * @Descprition 启动靓仔RPC功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 注册自定义的启动类
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableLzRPC {

	/**
	 * 是否需要启动 server
	 *
	 * @return
	 */
	boolean needServer() default true;

}
