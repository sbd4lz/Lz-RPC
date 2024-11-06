package com.liangzai.example.consumer;

import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.proxy.ServiceProxyFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleConsumerApplication.class, args);

		User user = new User();
		user.setName("dogpei");
		RpcApplication.init();
		// 调用   todo 用注解实现
		UserService userService = ServiceProxyFactory.getProxy(UserService.class);
		User newUser = userService.getUser(user);
		userService.getUser(user);
		userService.getUser(user);
		if (newUser != null) {
			System.out.println(newUser.getName());
		} else {
			System.out.println("user == null");
		}

	}

}
