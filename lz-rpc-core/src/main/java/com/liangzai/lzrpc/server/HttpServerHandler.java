package com.liangzai.lzrpc.server;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.registry.LocalRegistry;
import com.liangzai.lzrpc.serizalizer.JdkSerializer;
import com.liangzai.lzrpc.serizalizer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Author dengpei
 * @Date 2024/10/29 19:41
 * @Descprition
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
	/**
	  反序列化请求为对象，并从请求对象中获取参数。
	  根据服务名称从本地注册器中获取到对应的服务实现类。
	  通过反射机制调用方法，得到返回结果。
	  对返回结果进行封装和序列化，并写入到响应中。
 	*/
	@Override
	public void handle(HttpServerRequest request) {
		// 指定序列化器
		final JdkSerializer jdkSerializer = new JdkSerializer();
		// 记录日志
		System.out.println("Received request: " + request.method() + " " + request.uri());

		// 异步读取 HTTP 请求主体，读取完成后触发回调函数
		request.bodyHandler(body -> {
			RpcRequest rpcRequest = null;
			try {
				rpcRequest = jdkSerializer.deserialize(body.getBytes(), RpcRequest.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			// 构造响应结果对象
			RpcResponse rpcResponse = new RpcResponse();
			// 如果请求为 null，直接返回
			if (rpcRequest == null) {
				rpcResponse.setMessage("rpcRequest is null");
				doResponse(request, rpcResponse, jdkSerializer);
				return;
			}
			try {
				// 获取要调用的服务实现类，通过反射调用
				Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
				Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
				Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
				// 封装返回结果
				rpcResponse.setData(result);
				rpcResponse.setDataType(method.getReturnType());
				rpcResponse.setMessage("ok");
			} catch (Exception e) {
				e.printStackTrace();
				rpcResponse.setMessage(e.getMessage());
				rpcResponse.setException(e);
			}
			// 返回响应
			doResponse(request, rpcResponse, jdkSerializer);
		});
	}

	void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
		HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
		try {
			// 序列化
			byte[] serialized = serializer.serialize(rpcResponse);
			httpServerResponse.end(Buffer.buffer(serialized));
		} catch (IOException e) {
			e.printStackTrace();
			httpServerResponse.end(Buffer.buffer());
		}
	}


}
