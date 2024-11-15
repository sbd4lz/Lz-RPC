package com.liangzai.lzrpc;

import com.liangzai.lzrpc.protocol.NettyProtocolMessageDecoder;
import com.liangzai.lzrpc.protocol.ProtocolMessage;
import com.liangzai.lzrpc.protocol.ProtocolMessageSerializerEnum;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/11/15 17:17
 * @Descprition
 */
public class NettyTcpClientTests {

	public static ByteBuf encode(ProtocolMessage<?> protocolMessage) throws IOException {

		if (protocolMessage == null || protocolMessage.getHeader() == null) {
			return Unpooled.buffer();
		}
		ByteBuf buffer = Unpooled.buffer();
		ProtocolMessage.Header header = protocolMessage.getHeader();
		// 向缓冲区写入字节

		buffer.writeByte(header.getMagic());
		buffer.writeByte(header.getVersion());
		buffer.writeByte(header.getSerializer());
		buffer.writeByte(header.getType());
		buffer.writeByte(header.getStatus());
		buffer.writeLong(header.getRequestId());
		// 获取序列化器
		ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
		if (serializerEnum == null) {
			throw new RuntimeException("序列化协议不存在");
		}
		Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
		byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
		// 写入 body 长度和数据
		buffer.writeInt(bodyBytes.length);
		buffer.writeBytes(bodyBytes);
		return buffer;
	}

	public class NettyTcpClientHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for (int i = 0; i < 500; i++) {
				String s = "Hello Server! Hello Server! Hello Server! Hello Server! Hello Server!";
				ByteBuf buffer = Unpooled.copiedBuffer(s.getBytes());
				ctx.writeAndFlush(buffer);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ctx.close();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}

	@Test
	Promise<ByteBuf> clientTest() throws InterruptedException, IOException {

		String host = "localhost"; // 目标服务器地址
		int port = 8080;           // 目标服务器端口
		EventLoopGroup group = new NioEventLoopGroup();
		final Promise<ByteBuf> promise = new DefaultPromise<>(group.next());
		try {
			// 创建Bootstrap对象
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)  // 使用NioSocketChannel类，适用于TCP客户端
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 添加编码器、解码器等处理器
							ch.pipeline().addLast(new NettyTcpClientHandler());
						}
					});

			// 连接到服务器
			ChannelFuture future = bootstrap.connect(host, port).sync();


//			RpcRequest rpcRequest = RpcRequest.builder().build();
//			ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//			ProtocolMessage.Header header = new ProtocolMessage.Header();
//			header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//			header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//			header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
//			header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//			// 生成全局请求 ID
//			header.setRequestId(IdUtil.getSnowflakeNextId());
//			protocolMessage.setHeader(header);
//			protocolMessage.setBody(rpcRequest);
//			ByteBuf encode = encode(protocolMessage);
//			future.channel().writeAndFlush(encode);


			// 等待收到返回结果后关闭客户端连接
			future.channel().closeFuture().sync();
			return promise;

		} finally {
			// 关闭线程池
			group.shutdownGracefully();
		}
	}


	@Test
	void syncTest() throws InterruptedException, IOException {
		Promise<ByteBuf> promise = clientTest();

		promise.addListener(future -> {	 // 遇到异常或promise被设置为success时，才会触发
			if (future.isSuccess()) {
				ByteBuf in = (ByteBuf)future.getNow();
				ProtocolMessage<?> decoded = NettyProtocolMessageDecoder.decode(in);
				System.out.println("完成了老弟" + decoded.getBody());
			} else {
				System.out.println("Request failed: " + future.cause());
			}
		});
	}
}
