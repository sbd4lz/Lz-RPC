package com.liangzai.lzrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.loadbalancer.LoadBalancer;
import com.liangzai.lzrpc.loadbalancer.LoadBalancerFactory;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/10/31 9:25
 * @Descprition
 */
public class HttpServiceProxy implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

		String serviceName = method.getDeclaringClass().getName();
		RpcRequest rpcRequest = RpcRequest.builder()
				.serviceName(serviceName)
				.methodName(method.getName())
				.parameterTypes(method.getParameterTypes())
				.args(args)
				.build();

		try {
			byte[] serialized = serializer.serialize(rpcRequest);
			byte[] result;

			RpcConfig rpcConfig = RpcApplication.getRpcConfig();
			Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

			// note 根据 服务名称 到注册中心获取 服务地址
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(serviceName);
			serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
			Map<String, ServiceMetaInfo> serviceMetaInfoMap = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
			if (CollUtil.isEmpty(serviceMetaInfoMap)) {
				throw new RuntimeException("暂无服务地址");
			}

			// 负载均衡
			LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
			// 将调用方法名（请求路径）作为负载均衡参数 todo 修改
			Map<String, Object> requestParams = new HashMap<>();
			requestParams.put("methodName", rpcRequest.getMethodName());
			ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoMap);

			try(HttpResponse response = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
					.body(serialized)
					.execute()) {
				result = response.bodyBytes();
			}
			RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
			return rpcResponse.getData();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
