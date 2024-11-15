package com.liangzai.example.consumer;

import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.annotation.LzRPCReference;
import org.springframework.stereotype.Service;

/**
 * @Author dengpei
 * @Date 2024/11/11 20:33
 * @Descprition
 */
@Service
public class ExampleServiceImpl {

	@LzRPCReference
	private UserService userService;

	public void test() {
		User user = new User();
		user.setName("DOG 沛 DOG 沛 DOG 沛 DOG 沛 DOG 沛 DOG 沛 DOG 沛 DOG 沛 DOG 沛");
		User resultUser = userService.getUser(user);
		System.out.println(resultUser.getName());
	}


}
