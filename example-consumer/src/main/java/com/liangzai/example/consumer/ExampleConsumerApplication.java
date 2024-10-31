package com.liangzai.example.consumer;

import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleConsumerApplication.class, args);
		UserService userService = new UserServiceProxy();
		User user = new User();
		user.setName("dogpei");
		// 调用
		User newUser = userService.getUser(user);
		if (newUser != null) {
			System.out.println(newUser.getName());
		} else {
			System.out.println("user == null");
		}

	}

}
