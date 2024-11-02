package com.liangzai.lzrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author dengpei
 * @Date 2024/10/31 9:25
 * @Descprition
 */
public class ServiceProxy implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

		RpcRequest rpcRequest = RpcRequest.builder()
				.serviceName(method.getDeclaringClass().getName())
				.methodName(method.getName())
				.parameterTypes(method.getParameterTypes())
				.args(args)
				.build();

		try {
			byte[] serialized = serializer.serialize(rpcRequest);
			byte[] result;
			String url = RpcApplication.getRpcConfig().getServerHost() + ":" + RpcApplication.getRpcConfig().getServerPort();
			try(HttpResponse response = HttpRequest.post(url)
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
