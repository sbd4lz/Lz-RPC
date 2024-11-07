package com.liangzai.lzrpc.protocol;

import lombok.Getter;

/**
 * @Author dengpei
 * @Date 2024/11/7 15:23
 * @Descprition 协议消息状态枚举类
 */
@Getter
public enum ProtocolMessageStatusEnum {

	OK("ok", 20),
	BAD_REQUEST("badRequest", 40),
	BAD_RESPONSE("badResponse", 50);

	private final String text;

	private final int value;

	ProtocolMessageStatusEnum(String text, int value) {
		this.text = text;
		this.value = value;
	}

	/**
	 * 根据 value 获取枚举
	 *
	 * @param value
	 * @return
	 */
	public static ProtocolMessageStatusEnum getEnumByValue(int value) {
		for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
			if (anEnum.value == value) {
				return anEnum;
			}
		}
		return null;
	}


}
