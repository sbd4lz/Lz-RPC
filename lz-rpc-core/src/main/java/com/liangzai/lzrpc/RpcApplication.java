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
		// 配置初始化
		rpcConfig = newRpcConfig;
		log.info("rpc init, config = {}", newRpcConfig.toString());
		// 注册中心初始化
		RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
		Registry registry = RegistryFactory.getInstance(registryConfig.getType());
		registry.init(registryConfig);
		log.info("registry init, config = {}", registryConfig);

		// note 创建并注册 Shutdown Hook，JVM 退出时执行操作
		Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
	}

	public static void init(){
		RpcConfig newRpcConfig;
		RegistryConfig registryConfig;
		try{
			registryConfig = ConfigUtils.loadConfig(RegistryConfig.class, RpcConstant.DEFAULT_REGISTRY_CONFIG_PREFIX);
			newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
			newRpcConfig.setRegistryConfig(registryConfig);
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
