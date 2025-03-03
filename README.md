# 靓仔高性能可扩展RPC框架

### Java + Etcd + Netty(Vert.x) 自定义RPC传输协议的RPC实现，提供多种序列化方式、负载均衡策略、重试策略、容错策略，注解驱动、简单易用。


* 高性能机制：
* * 轮询、随机、一致性哈希负载均衡策略；
* * 固定间隔重试、指数间隔重试、斐波那契间隔重试策略；
* * failover、failfast、failback、failsafe 容错策略。
* * 服务提供方采用心跳检测保活服务节点，服务消费方本地缓存并监听在线服务节点。
* 可扩展机制：支持开发者通过SPI机制自定义注册中心等多种细节实现。
* 自定义协议请求头： 

  | Magic | Version | Serializer | Type  | Status | RPC Request ID | Data Length |
  |:-----:|:-------:|:----------:|:-----:|:------:|:--------------:|:-----------:|
  | 8 bit |  8 bit  |   8 bit    | 8 bit | 8 bit  |     64 bit     |   32 bit    |

## 配置方法
### 1.在服务消费者和服务提供者模块中引入Lz-Rpc：
```yaml
<dependency>
    <groupId>com.liangzai</groupId>
    <artifactId>lz-rpc-core</artifactId>
    <version>0.0.1</version>
<dependency>
```

### 2. 相关注解
```java
@EnableLzRPC        // 写在SpringBoot启动类上，初始化LzRPC
@LzRPCReference     // 服务消费者实例
@lzRPCService       // 服务提供者类
```

### 3. 配置文件
示例
```properties
rpc.name=lz-rpc             # RPC框架名称
rpc.version=1.0.1           # RPC框架版本
rpc.serverHost=localhost    # 当前结点的主机名
rpc.serverPort=8080         # 当前结点的端口
rpc.mock=false              # 是否开启接口Mock
rpc.serializer=jdk          # 序列化方式 (jdk | hessian | json | kryo)
rpc.loadBalancer=random     # 负载均衡策略 (随机 random | 轮询 roundRobin | 一致性哈希 consistentHash)
rpc.retryStrategy=no        # 重试策略 (不重试 no | 随机时间重试 random | 固定时间重试 fix | 斐波那契时间重试 fibo)
# RPC 注册中心配置 (默认实现etcd)
rpc.registry.type=etcd                          # 注册中心种类
rpc.registry.address=127.0.0.1:2379             # 注册中心地址
rpc.registry.username=liangzai                  # 用户名
rpc.registry.password=123456                    # 密码
rpc.registry.timeout=10000                      # 超时时间(毫秒)
```

## 模块介绍
* example-consumer    服务消费者示例
* example-provider    服务提供者示例
* example-interface   服务接口示例
* lz-rpc-core         lz-RPC 框架核心

## 自定义配置
Lz-RPC 支持通过SPI机制自定义注册中心、序列化器、负载均衡策略、重试策略、容错策略。


以自定义ZooKeeper注册中心为例：
### 1. 在 Resources/META-INF/rpc/ 目录下创建文件
* 注册中心: com.liangzai.lzrpc.registry.Registry
* 序列化器: com.liangzai.lzrpc.registry.Serializer
* 负载均衡: com.liangzai.lzrpc.fault.retry.LoadBlancer
* 重试策略: com.liangzai.lzrpc.fault.retry.RetryStrategy
* 容错策略: com.liangzai.lzrpc.registry.TolerantStrategy
### 2. 根据自定义类编写文件内容
```text
key=value
键名=引用路径
zookeeper=com.xxxxx.xxxxx.registry.ZookeeperRegistry
```

### 3. 修改配置文件
```properties
rpc.registry.type=zookeeper
rpc.registry.address=127.0.0.1:2181
...                  
```

### 4. 实现com.liangzai.lzrpc.registry.Registry接口
```java
public class ZookeeperRegistry implements Registry{
	// 初始化
	@Override
	public void init(RegistryConfig registryConfig) {
	}
	
	// 注册服务
	@Override
	public boolean register(ServiceMetaInfo serviceMetaInfo) throws Exception {
		return false;
	}
	
	// 注销服务
	@Override
	public void unRegister(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
	}
	
	// 服务发现
	@Override
	public Map<String, ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException {
		return null;
	}
	
	// 服务销毁
	@Override
	public void destroy() {
	}
}
```
