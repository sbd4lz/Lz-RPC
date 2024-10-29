package com.liangzai.example.provider;


import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;

/**
 * @Author dengpei
 * @Date 2024/10/29 16:44
 * @Descprition
 */
public class UserServiceImpl implements UserService {
	@Override
	public User getUser(User user) {
		System.out.println("用户名：" + user.getName());
		return user;
	}
}
