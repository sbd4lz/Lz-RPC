package com.liangzai.example.provider;


import com.liangzai.exampleinterface.model.User;
import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.annotation.LzRPCService;
import org.springframework.stereotype.Service;

/**
 * @Author dengpei
 * @Date 2024/10/29 16:44
 * @Descprition
 */
@Service
@LzRPCService
public class UserServiceImpl implements UserService {
	@Override
	public User getUser(User user) {
		System.out.println("用户名：" + user.getName());
		return user;
	}
}
