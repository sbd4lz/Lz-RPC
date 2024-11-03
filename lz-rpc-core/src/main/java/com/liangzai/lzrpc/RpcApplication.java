package com.liangzai.lzrpc;

import com.liangzai.lzrpc.config.RegistryConfig;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.constant.RpcConstant;
import com.liangzai.lzrpc.registry.Registry;
import com.liangzai.lzrpc.registry.RegistryFactory;
import com.liangzai.lzrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author dengpei
 * @Date 2024/10/31 16:00
 * @Descprition
 */
@Slf4j
public class RpcApplication {

	private static volatile RpcConfig rpcConfig;

	public static void init(RpcConfig newRpcConfig){
		rpcConfig = newRpcConfig;
		log.info("rpc init, config = {}", newRpcConfig.toString());
		// 注册中心初始化
		RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
		Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
		registry.init(registryConfig);
		log.info("registry init, config = {}", registryConfig);
	}

	public static void init(){
		RpcConfig newRpcConfig;
		try{
			newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
		}catch (Exception e){
			// 加载失败，使用默认配置
			newRpcConfig = new RpcConfig();
		}
		init(newRpcConfig);
	}

	public static RpcConfig getRpcConfig(){
		if(rpcConfig == null){
			synchronized (RpcApplication.class){
				if(rpcConfig == null){
					init();
				}
			}
		}
		return rpcConfig;
	}

}
