package com.liangzai.lzrpc;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.protocol.ProtocolConstant;
import com.liangzai.lzrpc.protocol.ProtocolMessage;
import com.liangzai.lzrpc.protocol.ProtocolMessageSerializerEnum;
import com.liangzai.lzrpc.protocol.ProtocolMessageTypeEnum;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author dengpei
 * @Date 2024/11/14 16:04
 * @Descprition
 */

public class NettyTcpServerTests {
	public static ProtocolMessage<?> decode(ByteBuf buffer) throws IOException {
		ProtocolMessage.Header header = new ProtocolMessage.Header();
		byte magic = buffer.getByte(0);
		// 校验魔数
		if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
			throw new RuntimeException("消息 magic 非法");
		}
		// 分别从指定位置读出 Buffer
		header.setMagic(magic);
		header.setVersion(buffer.getByte(1));
		header.setSerializer(buffer.getByte(2));
		header.setType(buffer.getByte(3));
		header.setStatus(buffer.getByte(4));
		header.setRequestId(buffer.getLong(5));
		header.setBodyLength(buffer.getInt(13));
		// 防止粘包问题，只读指定长度的数据
		byte[] bodyBytes = new byte[header.getBodyLength()];
		buffer.getBytes(17, bodyBytes);
		// 解析消息体
		ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
		if (serializerEnum == null) {
			throw new RuntimeException("序列化消息的协议不存在");
		}
		Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
		ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
		if (messageTypeEnum == null) {
			throw new RuntimeException("序列化消息的类型不存在");
		}
		switch (messageTypeEnum) {
			case REQUEST:
				RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
				return new ProtocolMessage<>(header, request);
			case RESPONSE:
				RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
				return new ProtocolMessage<>(header, response);
			case HEART_BEAT:
			case OTHERS:
			default:
				throw new RuntimeException("暂不支持该消息类型");
		}
	}

	public class TcpServerHandler extends ChannelInboundHandlerAdapter{
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
			ByteBuf in = (ByteBuf)msg;
//			ProtocolMessage<?> decode = decode(in);
			System.out.println("Received message: " + in.toString(Charset.defaultCharset()) + '\n');
			// 给客户端发送响应

		}
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{

		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();  // 发生异常时关闭连接
		}
	}


	@Test
	void serverTest() throws InterruptedException{
		// NIO 线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 用于接收客户端连接的线程组
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理 I/O 操作的线程组, 默认为CPU核心数*2
		try {
			// 使用 ServerBootstrap 配置和启动服务端
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					// 父通道类型, 子通道类型会根据父类型推断
					.channel(NioServerSocketChannel.class)
					// option() 用于设置父通道的参数 childOption
					.option(ChannelOption.SO_BACKLOG, 100)  // SO_BACKLOG 指定TCP三次握手中最大连接个数，包括半连接状态队列和全连接队列。
					// childOption() 用于设置接受连接的子通道Channel上的参数，客户端无作用。
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					// handler() 设置父通道的处理器，处理连接请求以及连接本身的管理工作，如连接建立、断开。
					.handler(new LoggingHandler(LogLevel.INFO))
					// childHandler() 设置子通道的处理器，处理每个客户端连接的业务处理，如消息解码、消息处理、应答。
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
//							socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 17, 4, 0, 4));
							socketChannel.pipeline().addLast(new TcpServerHandler());
						}
					});
			// 绑定端口，同步等待成功
			ChannelFuture f = bootstrap.bind(8080).sync();
			// 等待所有服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}


}
