package com.liangzai.lzrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.fault.retry.RetryStrategy;
import com.liangzai.lzrpc.fault.retry.RetryStrategyFactory;
import com.liangzai.lzrpc.fault.tolerant.TolerantStrategy;
import com.liangzai.lzrpc.fault.tolerant.TolerantStrategyFactory;
import com.liangzai.lzrpc.loadbalancer.LoadBalancer;
import com.liangzai.lzrpc.loadbalancer.LoadBalancerFactory;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/7 17:23
 * @Descprition Tcp服务代理
 */
public class TcpServiceProxy implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args){

		String serviceName = method.getDeclaringClass().getName();
		RpcRequest rpcRequest = RpcRequest.builder()
				.serviceName(serviceName)
				.methodName(method.getName())
				.parameterTypes(method.getParameterTypes())
				.args(args)
				.build();

		RpcResponse rpcResponse;
		RpcConfig rpcConfig = RpcApplication.getRpcConfig();;
		try {
			// 从注册中心获取服务提供者请求地址 todo 处理重复项
			Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(serviceName);
			serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
			Map<String, ServiceMetaInfo> serviceMetaInfoMap = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
			if(CollUtil.isEmpty(serviceMetaInfoMap)){
				throw new RuntimeException("暂无服务地址");
			}
			// 负载均衡
			LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
			// 将调用方法名（请求路径）作为负载均衡参数
			Map<String, Object> requestParams = new HashMap<>();
			requestParams.put("methodName", rpcRequest.getMethodName());
			ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoMap);

			// rpc 请求
			// 使用重试机制
			RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
			rpcResponse = retryStrategy.doRetry(() ->
					VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
			);

		} catch (Exception e) {
			// 容错机制
			TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
			rpcResponse = tolerantStrategy.doTolerant(null, e);
		}
		return rpcResponse.getData();
	}
}
