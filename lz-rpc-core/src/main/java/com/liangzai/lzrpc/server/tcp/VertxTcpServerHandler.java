package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.protocol.ProtocolMessage;
import com.liangzai.lzrpc.protocol.VertxProtocolMessageDecoder;
import com.liangzai.lzrpc.protocol.VertxProtocolMessageEncoder;
import com.liangzai.lzrpc.protocol.ProtocolMessageTypeEnum;
import com.liangzai.lzrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Author dengpei
 * @Date 2024/11/7 17:08
 * @Descprition tcp请求处理
 */
public class VertxTcpServerHandler implements Handler<NetSocket> {

	@Override
	public void handle(NetSocket netSocket) {
		TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
			// 默认消息类型为 RpcRequest
			ProtocolMessage<RpcRequest> protocolMessage;
			try {
				protocolMessage = (ProtocolMessage<RpcRequest>) VertxProtocolMessageDecoder.decode(buffer);
			} catch (IOException e) {
				throw new RuntimeException("协议消息解码错误");
			}
			RpcResponse rpcResponse = new RpcResponse();
			try {
				RpcRequest rpcRequest = protocolMessage.getBody();
				// fixme 通过反射创建实例只能调用无参构造函数，对于有参构造函数或者需要自动注入的属性，这种方式就行不通了。
				Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
				Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
				Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
				rpcResponse.setData(result);
				rpcResponse.setDataType(method.getReturnType());
				rpcResponse.setMessage("ok");
			} catch (Exception e) {
				e.printStackTrace();
				rpcResponse.setMessage(e.getMessage());
				rpcResponse.setException(e);
			}

			ProtocolMessage.Header header = protocolMessage.getHeader();
			header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
			ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
			try {
				Buffer encode = VertxProtocolMessageEncoder.encode(responseProtocolMessage);
				netSocket.write(encode);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		netSocket.handler(bufferHandlerWrapper);

	}
}
