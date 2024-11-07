package com.liangzai.lzrpc.protocol;

/**
 * @Author dengpei
 * @Date 2024/11/7 15:18
 * @Descprition 协议常量
 */
public class ProtocolConstant {

	/**
	 * 消息头长度
	 */
	public static final int MESSAGE_HEADER_LENGTH = 17;

	/**
	 * 协议魔数
	 */
	public static final byte PROTOCOL_MAGIC = 0x1;

	/**
	 * 协议版本号
	 */
	public static final byte PROTOCOL_VERSION = 0x1;
}
