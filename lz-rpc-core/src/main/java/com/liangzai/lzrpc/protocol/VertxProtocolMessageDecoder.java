package com.liangzai.lzrpc.protocol;

import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.model.RpcResponse;
import com.liangzai.lzrpc.serializer.Serializer;
import com.liangzai.lzrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/11/7 16:20
 * @Descprition 协议消息解码器
 */
public class VertxProtocolMessageDecoder {

	public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
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
		byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
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
}
