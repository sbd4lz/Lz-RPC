package com.liangzai.lzrpc.serializer;

import com.liangzai.lzrpc.config.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/1 17:15
 * @Descprition
 */
public class SerializerFactory {

	static {
		SpiLoader.load(Serializer.class);
	}

	/**
	 * 序列化器，用于实现单例
	 * 	第一层大括号 {} 是创建 HashMap 的匿名子类的语法。
	 *   	它在 new HashMap<>() 后加了一个大括号，实际上是创建了一个 HashMap 的匿名内部类，而不是直接使用 HashMap 类本身。
	 * 	第二层大括号 {} 则是一个实例初始化块（instance initializer block），它允许在匿名内部类的构造函数中添加一些初始化代码。
	 *   	在这里，它用来在创建 HashMap 时直接向 KEY_SERIALIZER_MAP 中添加初始数据，即调用 put 方法。
	 */
	@Deprecated
	private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
		put(SerializerKeys.JDK, new JdkSerializer());
		put(SerializerKeys.JSON, new JsonSerializer());
		put(SerializerKeys.KRYO, new KryoSerializer());
		put(SerializerKeys.HESSIAN, new HessianSerializer());
	}};

	/**
	 * 默认序列化器
	 */
	private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

	/**
	 * 获取实例
	 * @param key
	 * @return
	 */
	public static Serializer getInstance(String key) {
		return SpiLoader.getInstance(Serializer.class, key);
	}

}
