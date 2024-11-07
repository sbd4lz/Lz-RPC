package com.liangzai.lzrpc.protocol;

import lombok.Getter;

/**
 * @Author dengpei
 * @Date 2024/11/7 15:31
 * @Descprition 协议消息类型枚举类
 */
@Getter
public enum ProtocolMessageTypeEnum {
	REQUEST(0),
	RESPONSE(1),
	HEART_BEAT(2),
	OTHERS(3);

	private final int key;

	ProtocolMessageTypeEnum(int key) {
		this.key = key;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key
	 * @return
	 */
	public static ProtocolMessageTypeEnum getEnumByKey(int key) {
		for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
			if (anEnum.key == key) {
				return anEnum;
			}
		}
		return null;
	}

}
