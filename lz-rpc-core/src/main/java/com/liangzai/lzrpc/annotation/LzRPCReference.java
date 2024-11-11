package com.liangzai.lzrpc.annotation;

import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.fault.retry.RetryStrategyKeys;
import com.liangzai.lzrpc.fault.tolerant.TolerantStrategyKeys;
import com.liangzai.lzrpc.loadbalancer.LoadBalancerKeys;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author dengpei
 * @Date 2024/11/11 17:29
 * @Descprition 服务消费者注解（用于注入服务）
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface LzRPCReference {

	/**
	 * 服务接口类
	 */
	Class<?> interfaceClass() default void.class;

	/**
	 * 版本
	 */
	String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

	/**
	 * 负载均衡器
	 */
	String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

	/**
	 * 重试策略
	 */
	String retryStrategy() default RetryStrategyKeys.NO;

	/**
	 * 容错策略
	 */
	String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

	/**
	 * 模拟调用
	 */
	boolean mock() default false;
}
