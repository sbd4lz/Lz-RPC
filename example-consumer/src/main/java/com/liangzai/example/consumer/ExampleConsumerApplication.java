package com.liangzai.example.consumer;

import com.liangzai.lzrpc.annotation.EnableLzRPC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Kitsch
 */
@EnableLzRPC(needServer = false)
@SpringBootApplication
public class ExampleConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleConsumerApplication.class, args);
	}


}
