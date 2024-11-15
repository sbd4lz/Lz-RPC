package com.liangzai.lzrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.model.ServiceMetaInfo;
import com.liangzai.lzrpc.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author dengpei
 * @Date 2024/11/15 19:14
 * @Descprition
 */
@Slf4j
public class NettyTcpClient {

	public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, IOException, ExecutionException {
		// 构造请求体，发送 TCP 请求
		ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
		ProtocolMessage.Header header = new ProtocolMessage.Header();
		header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
		header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
		header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
		header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
		// 生成全局请求 ID
		header.setRequestId(IdUtil.getSnowflakeNextId());
		protocolMessage.setHeader(header);
		protocolMessage.setBody(rpcRequest);
		ByteBuf encodedMessage = NettyProtocolMessageEncoder.encode(protocolMessage);

		CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
		Promise<ByteBuf> promise = doTcpRequest(encodedMessage, serviceMetaInfo);	// 此处会阻塞直到返回结果
		promise.addListener(future -> {
			if (future.isSuccess()) {
				ByteBuf buffer = (ByteBuf) future.getNow();
				ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
						(ProtocolMessage<RpcResponse>) NettyProtocolMessageDecoder.decode(buffer);
				responseFuture.complete(rpcResponseProtocolMessage.getBody());
			} else {
				log.info("Request failed: " + future.cause());
			}
		});
		return responseFuture.get();
	}
	private static Promise<ByteBuf> doTcpRequest(ByteBuf protocolMessage, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, IOException {
		EventLoopGroup group = new NioEventLoopGroup();
		//  针对每个线程创建一个Promise，线程安全
		final Promise<ByteBuf> promise = new DefaultPromise<>(group.next());
		try {
			// 创建Bootstrap对象
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)  // 使用NioSocketChannel类，适用于TCP客户端
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 添加处理器
							ch.pipeline().addLast(new NettyTcpClientHandler(promise));
						}
					});
			// 连接到服务器
			ChannelFuture future = bootstrap.connect(serviceMetaInfo.getServiceHost(), serviceMetaInfo.getServicePort()).sync();
			future.channel().writeAndFlush(protocolMessage);
			// 等待收到返回结果后关闭客户端连接
			future.channel().closeFuture().sync();
			return promise;
		} finally {
			// 关闭线程池
			group.shutdownGracefully();
		}
	}

}
