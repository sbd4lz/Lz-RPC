package com.liangzai.lzrpc.serializer;



import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

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
		Hessian2Output ho = new Hessian2Output(bos);
		ho.writeObject(object);
		ho.flush();
		return bos.toByteArray();
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		Hessian2Input hi = new Hessian2Input(bis);
		return (T) hi.readObject(tClass);
	}
}
