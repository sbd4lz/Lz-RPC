package com.liangzai.lzrpc.protocol;

import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/11/15 19:20
 * @Descprition
 */
public class NettyProtocolMessageEncoder {

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
}
