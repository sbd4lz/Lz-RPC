package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.protocol.NettyProtocolMessageDecoder;
import com.liangzai.lzrpc.protocol.NettyProtocolMessageEncoder;
import com.liangzai.lzrpc.protocol.ProtocolMessage;
import com.liangzai.lzrpc.protocol.ProtocolMessageTypeEnum;
import com.liangzai.lzrpc.registry.LocalRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Kitsch
 */
@Slf4j
public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
			ByteBuf buffer = (ByteBuf)msg;
			ProtocolMessage<RpcRequest> protocolMessage;
			try {
				protocolMessage = (ProtocolMessage<RpcRequest>) NettyProtocolMessageDecoder.decode(buffer);
			} catch (IOException e) {
				throw new RuntimeException("协议消息解码错误");
			}
			RpcResponse rpcResponse = new RpcResponse();
			try {
				RpcRequest rpcRequest = protocolMessage.getBody();
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
				ByteBuf encode = NettyProtocolMessageEncoder.encode(responseProtocolMessage);
				ctx.writeAndFlush(encode);  // 发送消息到客户端
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();  // 发生异常时关闭连接
		}
	}