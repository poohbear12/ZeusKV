package com.cc.raft.raft;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RaftServer {
    private static final Logger logger = LoggerFactory.getLogger(RaftServer.class);
    
    private final int port;
    private final Server server;
    private final RaftNode raftNode;
    
    public RaftServer(int port, RaftNode raftNode) {
        this.port = port;
        this.raftNode = raftNode;
        
        this.server = ServerBuilder.forPort(port)
                .addService(new RaftServiceImpl(raftNode))
                .build();
    }
    
    public void start() throws IOException {
        server.start();
        logger.info("Raft服务器在端口 {} 启动", port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("JVM关闭，正在停止Raft服务器...");
            this.stop();
            logger.info("Raft服务器已停止");
        }));
    }
    
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
        
        if (raftNode != null) {
            raftNode.shutdown();
        }
    }
    
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    public static void main(String[] args) throws Exception {
        // 配置集群信息
        String nodeId = args[0];
        int port = Integer.valueOf(args[1]);
        
        List<String> peerIds = Arrays.asList("node1", "node2", "node3");
        Map<String, String> peerAddresses = new HashMap<>();
        peerAddresses.put("node1", "localhost:50051");
        peerAddresses.put("node2", "localhost:50052");
        peerAddresses.put("node3", "localhost:50053");
        logger.info("节点{}, 节点启动IP{}", nodeId, port);
        // 创建Raft节点
        RaftNode raftNode = new RaftNode(nodeId, peerIds, peerAddresses);
        
        // 启动服务器
        RaftServer server = new RaftServer(port, raftNode);
        server.start();
        server.blockUntilShutdown();
    }
}