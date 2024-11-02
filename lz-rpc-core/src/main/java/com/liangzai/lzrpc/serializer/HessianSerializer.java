package com.liangzai.lzrpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author dengpei
 * @Date 2024/11/1 17:17
 * @Descprition Hessian序列化器
 */
public class HessianSerializer implements Serializer{
	@Override
	public <T> byte[] serialize(T object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		HessianOutput ho = new HessianOutput(bos);
		ho.writeObject(object);
		return bos.toByteArray();
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		HessianInput hi = new HessianInput(bis);
		return (T) hi.readObject(tClass);
	}
}
