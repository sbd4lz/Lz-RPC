package com.liangzai.lzrpc;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author dengpei
 * @Date 2024/11/3 10:34
 * @Descprition
 */
public class JetcdCoreTest {
	@Test
	void kvTest() throws ExecutionException, InterruptedException {
		/**
		 * note try-with-resources 结合 Autocloseable 能实现资源的自动关闭，即离开try块自动调用close()
		 * KV 客户端实现了 AutoCloseable，并且它是由 Client 创建的，在关闭 Client 的时候，会同时关闭底层的 KV 客户端及其相关的资源。
		 * 这种设计允许开发者只需关注上层 Client 的关闭，而不必单独处理底层 KV 资源的关闭。
		 */
		try(Client client = Client.builder().endpoints("http://localhost:2379").build()){
			KV kvClient = client.getKVClient();
			ByteSequence key = ByteSequence.from("test_key".getBytes());
			ByteSequence value = ByteSequence.from("test_value".getBytes());
			// put the key-value
			kvClient.put(key, value).get();
			// get the CompletableFuture
			CompletableFuture<GetResponse> getFuture = kvClient.get(key);
			// get the value from CompletableFuture
			GetResponse response = getFuture.get();
			System.out.println(response);
			// delete the key
			kvClient.delete(key).get();
//			不需要 kvClient.close();
		}
	}

	@Test
	void leaseTest() throws ExecutionException, InterruptedException {
		try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {
			Lease leaseClient = client.getLeaseClient();
			leaseClient.grant(10).get();
			client.getAuthClient();
		}
	}
}
