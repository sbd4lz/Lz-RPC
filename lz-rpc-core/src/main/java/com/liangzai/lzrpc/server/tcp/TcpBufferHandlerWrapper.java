package com.liangzai.lzrpc.server.tcp;

import com.liangzai.lzrpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * @Author dengpei
 * @Date 2024/11/7 21:38
 * @Descprition  使用 recordParser 装饰 buffer, 处理半包和粘包问题
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
	// todo 理解此处的装饰器模式
	private final RecordParser recordParser;

	public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
		this.recordParser = initRecordParser(bufferHandler);
	}

	private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
		RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
		recordParser.setOutput(new Handler<Buffer>() {
			// 初始化
			int size = -1;
			// 一次完整的读取（头 + 体）
			Buffer resultBuffer = Buffer.buffer();
			@Override
			public void handle(Buffer buffer) {
				if(-1 == size){
					// 读取消息体长度（位于消息头的第13个字节）
					size = buffer.getInt(13);
					recordParser.fixedSizeMode(size);
					// 写入头信息到结果
					resultBuffer.appendBuffer(buffer);
				}else {
					// 写入体信息到结果
					resultBuffer.appendBuffer(buffer);
					// 已拼接为完整 Buffer，执行处理
					bufferHandler.handle(resultBuffer);
					// 重置一轮
					recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
					size = -1;
					resultBuffer = Buffer.buffer();
				}
			}
		});

		return recordParser;
	}

	@Override
	public void handle(Buffer buffer) {
		recordParser.handle(buffer);
	}
}
