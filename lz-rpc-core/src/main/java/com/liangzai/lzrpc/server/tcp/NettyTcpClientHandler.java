package com.liangzai.lzrpc.server.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;

/**
 * @Author dengpei
 * @Date 2024/11/15 19:25
 * @Descprition
 */
public class NettyTcpClientHandler extends ChannelInboundHandlerAdapter {

	private final Promise<ByteBuf> promise;

	public NettyTcpClientHandler(Promise<ByteBuf> promise){
		this.promise = promise;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		promise.setSuccess((ByteBuf) msg);
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
