package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author dengpei
 * @Date 2024/11/15 19:14
 * @Descprition
 */
@Slf4j
public class NettyTcpServer implements Server {

	@Override
	public void doStart(int port) throws InterruptedException {
		// NIO 线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 用于接收客户端连接的线程组
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理 I/O 操作的线程组, 默认为CPU核心数*2

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
						socketChannel.pipeline().addLast(new NettyTcpServerHandler());
					}
				});
		// 绑定端口，同步等待成功
		ChannelFuture f = bootstrap.bind(8080).sync();
		// 等待所有服务端监听端口关闭
		f.channel().closeFuture().addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				log.info("Server channel closed successfully.");
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			} else {
				log.info("Server channel closed with failure: " + future.cause());
			}
		});

//			f.channel().closeFuture().sync();  // 这会导致主线程阻塞

	}
}
