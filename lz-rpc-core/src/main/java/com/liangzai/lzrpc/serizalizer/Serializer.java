package com.liangzai.lzrpc.serizalizer;

import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/10/29 19:24
 * @Descprition
 */
public interface Serializer {
	/**
	 * 序列化
	 *
	 * @param object 对象
	 * @param <T>
	 * @return 字节流
	 * @throws IOException
	 */
	<T> byte[] serialize(T object) throws IOException;

	/**
	 * 反序列化
	 *
	 * @param bytes 字节流
	 * @param type 对象类型
	 * @param <T>
	 * @return 对象
	 * @throws IOException
	 */
	<T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
