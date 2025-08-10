package com.cc.raft.grpc;

import com.example.grpc.Raft.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;

public class HelloServer {
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        HelloServer server = new HelloServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new HelloServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);
        
        // 注册JVM关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            HelloServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    // 实现服务接口
    private static class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            // 构建响应
            HelloResponse response = HelloResponse.newBuilder()
                    .setMessage("Hello " + req.getName())
                    .build();
            // 返回响应
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}