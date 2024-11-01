package com.liangzai.lzrpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author dengpei
 * @Date 2024/11/1 15:13
 * @Descprition
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> returnType = method.getReturnType();
		log.info("mock interface {}", method.getName());
		return getDefaultObject(returnType);
	}

	/**
	 * 获取默认对象
	 * @param type
	 * @return
	 */
	public Object getDefaultObject(Class<?> type){
		if(type.isPrimitive()){
			if(type == boolean.class){
				return false;
			}else if(type == int.class){
				return 0;
			}else if(type == short.class){
				return (short) 0;
			}else if(type == long.class){
				return 0L;
			}
		}
		return null;
	}

}
