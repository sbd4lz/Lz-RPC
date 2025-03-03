package com.liangzai.lzrpc;

import cn.hutool.core.util.IdUtil;
import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.model.RpcRequest;
import com.liangzai.lzrpc.protocol.*;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/11/7 16:53
 * @Descprition
 */
public class ProtocolMessageTest {

	@Test
	public void testEncodeAndDecode() throws IOException {
		// 构造消息
		ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
		ProtocolMessage.Header header = new ProtocolMessage.Header();

		header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
		header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
		header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
		header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
		header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
		header.setRequestId(IdUtil.getSnowflakeNextId());
		header.setBodyLength(0);

		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setServiceName("myService");
		rpcRequest.setMethodName("myMethod");
		rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
		rpcRequest.setParameterTypes(new Class[]{String.class});
		rpcRequest.setArgs(new Object[]{"aaa", "bbb"});

		protocolMessage.setHeader(header);
		protocolMessage.setBody(rpcRequest);

		Buffer encodeBuffer = VertxProtocolMessageEncoder.encode(protocolMessage);
		ProtocolMessage<?> message = VertxProtocolMessageDecoder.decode(encodeBuffer);
		System.out.println(message);
		Assert.assertNotNull(message);
	}
}
