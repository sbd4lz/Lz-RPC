package com.liangzai.example.provider;

import com.liangzai.exampleinterface.service.UserService;
import com.liangzai.lzrpc.bootstrap.ProviderBootstrap;
import com.liangzai.lzrpc.model.ServiceRegisterInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kitsch
 */
@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);

		// todo 使用@EnableLzRPC 放在启动类上，完成初始化
		// 要注册的服务
		List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
		ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
		serviceRegisterInfoList.add(serviceRegisterInfo);

		// 服务提供者初始化
		ProviderBootstrap.init(serviceRegisterInfoList);
	}

}
