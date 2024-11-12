# 靓仔高性能RPC框架


### 用我写的框架你就死心吧, 保证把事给你办砸啰。

## 简单示例
### 1.在服务消费者和服务提供者模块中引入Lz-Rpc：
```yaml
<dependency>
    <groupId>com.liangzai</groupId>
    <artifactId>lz-rpc-core</artifactId>
    <version>0.0.1</version>
<dependency>
```

### 2. 编写RPC接口定义模块
2.1 可在接口定义模块的resources目录下

### 3. 添加相关注解
```java
@EnableLzRPC        // 写在SpringBoot启动类上，初始化LzRPC
@LzRPCReference     // 服务消费者实例
@lzRPCService       // 服务提供者类
```