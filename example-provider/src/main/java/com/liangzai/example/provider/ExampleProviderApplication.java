package com.liangzai.example.provider;

import com.liangzai.lzrpc.annotation.EnableLzRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Kitsch
 */
@EnableLzRPC
@SpringBootApplication
public class ExampleProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleProviderApplication.class, args);

//		// todo 使用@EnableLzRPC 放在启动类上，完成初始化
//		// 要注册的服务
//		List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
//		ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
//		serviceRegisterInfoList.add(serviceRegisterInfo);
//
//		// 服务提供者初始化
//		ProviderBootstrap.init(serviceRegisterInfoList);
	}

}
