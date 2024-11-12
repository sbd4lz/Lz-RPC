package com.liangzai.lzrpc.registry;

import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author dengpei
 * @Date 2024/11/11 21:56
 * @Descprition 测试注册中心
 */
public class TestRegistry implements Registry{
	@Override
	public void init(RegistryConfig registryConfig) {

	}

	@Override
	public boolean register(ServiceMetaInfo serviceMetaInfo) throws Exception {
		return false;
	}

	@Override
	public void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {

	}

	@Override
	public Map<String, ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException {
		return null;
	}

	@Override
	public void heartBeat() {

	}

	@Override
	public void watch(String serviceKey) {

	}

	@Override
	public void destroy() {

	}
}
