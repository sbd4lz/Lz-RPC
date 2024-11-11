package com.liangzai.lzrpc.bootstrap;

import com.liangzai.lzrpc.RpcApplication;
import com.liangzai.lzrpc.annotation.EnableLzRPC;
import com.liangzai.lzrpc.config.RpcConfig;
import com.liangzai.lzrpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author dengpei
 * @Date 2024/11/11 18:44
 * @Descprition
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

	/**
	 * Spring 初始化时执行，初始化 RPC 框架
	 *
	 * @param importingClassMetadata 表示导入该ImportBeanDefinitionRegistrar的@Configuration类的元数据，允许我们获取配置类上的注解信息。
	 * @param registry Bean定义注册器，允许我们将Bean定义手动注册到容器中
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 获取 EnableRpc 注解的属性值
		boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableLzRPC.class.getName())
				.get("needServer");

		// RPC 框架初始化（配置和注册中心）
		RpcApplication.init();

		// 全局配置
		final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

		// 启动服务器
		if (needServer) {
			VertxTcpServer vertxTcpServer = new VertxTcpServer();
			vertxTcpServer.doStart(rpcConfig.getServerPort());
		} else {
			log.info("不启动 server");
		}
	}
}
