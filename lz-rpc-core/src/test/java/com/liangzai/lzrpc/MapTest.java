package com.liangzai.lzrpc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/6 19:12
 * @Descprition
 */
public class MapTest {

	@Test
	void Test1(){
		Map<String, String> innerMap1 = new HashMap<>();
		innerMap1.put("inner11","1");
		innerMap1.put("inner12","2");
		innerMap1.put("inner13","3");
		Map<String, String> innerMap2 = new HashMap<>();
		innerMap2.put("inner21","1");
		innerMap2.put("inner22","2");
		innerMap2.put("inner23","3");

		Map<String, Map<String, String>> outMap = new HashMap<>();
		outMap.put("out1", innerMap1);
		outMap.put("out2", innerMap2);

		System.out.println(outMap);
		outMap.compute("out3", (k,v)->{

			if(v == null){
				v = new HashMap<>();
			}
			v.put("有的", "1111");
			return v;
		});
		System.out.println(outMap);

	}

}
