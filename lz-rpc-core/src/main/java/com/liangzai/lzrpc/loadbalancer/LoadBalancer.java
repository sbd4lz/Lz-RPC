package com.liangzai.lzrpc.loadbalancer;

import com.liangzai.lzrpc.model.ServiceMetaInfo;

import java.util.Map;

/**
 * @Author dengpei
 * @Date 2024/11/8 18:25
 * @Descprition  负载均衡器（消费端）
 */
// fixme 负载均衡的配置由消费端转移到注册中心
public interface LoadBalancer {

	/**
	 * 选择服务调用
	 *
	 * @param requestParams       请求参数
	 * @param serviceMetaInfoMap 可用服务Map
	 * @return
	 */
	ServiceMetaInfo select(Map<String, Object> requestParams, Map<String, ServiceMetaInfo> serviceMetaInfoMap);
}
