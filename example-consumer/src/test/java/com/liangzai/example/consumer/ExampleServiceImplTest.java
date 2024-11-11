package com.liangzai.example.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author dengpei
 * @Date 2024/11/11 20:35
 * @Descprition
 */
@SpringBootTest
public class ExampleServiceImplTest {

	@Resource
	private ExampleServiceImpl exampleService;

	@Test
	void test1() {
		exampleService.test();
	}
}
