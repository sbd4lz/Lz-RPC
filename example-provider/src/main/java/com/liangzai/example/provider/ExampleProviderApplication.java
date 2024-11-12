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
	}

}
