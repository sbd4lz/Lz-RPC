package com.liangzai.lzrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;
import com.liangzai.lzrpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/7 17:23
 * @Descprition
 */
public class TcpServiceProxy implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

		String serviceName = method.getDeclaringClass().getName();
		RpcRequest rpcRequest = RpcRequest.builder()
				.serviceName(serviceName)
				.methodName(method.getName())
				.parameterTypes(method.getParameterTypes())
				.args(args)
				.build();

		try {
			// 从注册中心获取服务提供者请求地址 todo 处理重复项
			RpcConfig rpcConfig = RpcApplication.getRpcConfig();
			Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(serviceName);
			serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
			Map<String, ServiceMetaInfo> serviceMetaInfoMap = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
			if(CollUtil.isEmpty(serviceMetaInfoMap)){
				throw new RuntimeException("暂无服务地址");
			}
			ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoMap.values().iterator().next();
			// 发送 TCP 请求
			RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
			return rpcResponse.getData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
