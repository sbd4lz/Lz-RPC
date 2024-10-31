package com.liangzai.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.serizalizer.JdkSerializer;
import com.liangzai.lzrpc.serizalizer.Serializer;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/10/30 16:05
 * @Descprition
 */
public class UserServiceProxy implements UserService {
	@Override
	public User getUser(User user) {
		Serializer serializer = new JdkSerializer();

		RpcRequest rpcRequest = RpcRequest.builder()
				.serviceName(UserService.class.getName())
				.methodName("getUser")
				.parameterTypes(new Class[]{User.class})
				.args(new Object[]{user})
				.build();

		try {
			byte[] serialized = serializer.serialize(rpcRequest);
			byte[] result;
			try(HttpResponse response = HttpRequest.post("http://localhost:8080").body(serialized).execute()){
				result = response.bodyBytes();
			}
			RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
			return (User)rpcResponse.getData();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
