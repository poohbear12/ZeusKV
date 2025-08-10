package com.cc.raft.grpc;

import com.example.grpc.Raft.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class HelloClient {
    private final ManagedChannel channel;
    private final HelloServiceGrpc.HelloServiceBlockingStub blockingStub;

    public HelloClient(String host, int port) {
        // 创建与服务端的通道（使用纯文本，非加密）
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        // 创建阻塞式存根
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // 调用服务
    public void greet(String name) {
        System.out.println("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response;
        
        try {
            // 调用服务方法
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            return;
        }
        
        System.out.println("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        // 默认连接本地50051端口
        HelloClient client = new HelloClient("localhost", 50051);
        try {
            // 调用服务
            String user = "World";
            if (args.length > 0) {
                user = args[0]; // 使用命令行参数作为用户名
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}