package com.liangzai.example.consumer;

import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.bootstrap.ConsumerBootstrap;
import com.liangzai.lzrpc.proxy.ServiceProxyFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleConsumerApplication.class, args);

		// todo 结点可能是消费者也可能是提供者，需要增加配置？
		ConsumerBootstrap.init();

		// 调用   todo 用注解实现
		User user = new User();
		user.setName("dogpei");
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
