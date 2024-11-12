# 靓仔高性能RPC框架

### Java + Etcd + Vert.x 自定义RPC传输协议的RPC实现，提供多种序列化方式、负载均衡策略和重试策略。

### 用我写的框架你就死心吧, 保证把事给你办砸啰。

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
# RPC 注册中心配置 (目前只支持etcd)
rpc.registry.address=http://localhost:2379      # 注册中心地址
rpc.registry.username=liangzai                  # 用户名
rpc.registry.password=123456                    # 密码
rpc.registry.timeout=10000                      # 超时时间(毫秒)
```

### 模块介绍
* example-consumer    服务消费者示例
* example-provider    服务提供者示例
* example-interface   服务接口示例
* lz-rpc-core         lz-RPC框架核心