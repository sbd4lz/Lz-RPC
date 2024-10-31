package com.liangzai.lzrpc.server;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.serizalizer.JdkSerializer;
import com.liangzai.lzrpc.serizalizer.Serializer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author dengpei
 * @Date 2024/10/31 17:42
 * @Descprition
 */
@Slf4j
public class HutoolHttpServerHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		log.info("Received request: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI().getPath());

		InputStream inputStream = httpExchange.getRequestBody();
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		RpcRequest rpcRequest;
		try {
			rpcRequest = (RpcRequest)objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			objectInputStream.close();
		}

		final JdkSerializer jdkSerializer = new JdkSerializer();

		RpcResponse rpcResponse = new RpcResponse();
			// 如果请求为 null，直接返回
		if (rpcRequest == null) {
			rpcResponse.setMessage("rpcRequest is null");
			doResponse(httpExchange, rpcResponse, jdkSerializer);
			return;
		}
		Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
		Method method;
		try {
			method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
			Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
			rpcResponse.setData(result);
			rpcResponse.setDataType(method.getReturnType());
			rpcResponse.setMessage("ok");
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			rpcResponse.setMessage(e.getMessage());
			rpcResponse.setException(e);
		}
		doResponse(httpExchange, rpcResponse, jdkSerializer);
	}

	private void doResponse(HttpExchange httpExchange, RpcResponse rpcResponse, Serializer serializer) throws IOException {
		// 输出响应的Header:
		byte[] response = serializer.serialize(rpcResponse);
		Headers respHeaders = httpExchange.getResponseHeaders();
		respHeaders.set("Content-Type", "text/html; charset=utf-8");
		respHeaders.set("Cache-Control", "no-cache");
		// 设置响应头
		httpExchange.sendResponseHeaders(200, 0);
		// 输出响应的内容:
		try (OutputStream out = httpExchange.getResponseBody()) {
			out.write(response);
		}
	}

}
