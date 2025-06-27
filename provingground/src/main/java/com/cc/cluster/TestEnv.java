package com.cc.cluster;

import com.cc.cluster.node.Node;
import com.cc.cluster.node.TLocalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ZeusKV
 * @description: 集群测试环境
 * @author: ccstar
 * @create: 2025-06-25  12:37
 * @create: 2025-06-25  12:37
 * @create: 2025-06-25  12:37
 **/


public class TestEnv {

    public static final Map<String, Node> map = new HashMap<>();


    /**
     * 1. 我们有三个节点
     * 2. 我们有多种类型的消息
     * 3. 每个节点都有一个start方法
     * 4. 我们通过线程来执行每个节点的方法
     * 5. 节点直接互相通信并且组成集群
     * 6. 需要编写逻辑
     *  - 消息发送
     *  - 消息处理
     */



    /**
     * 1. 构建三个节点
     * 2. 模拟选举过程
     * 3. 模拟心跳
     */
    public static void main(String[] args) {
        // 1. cluster
        // 192.168.14.100
        // 192.168.14.101
        // 192.168.14.102
        List<String> clusters1 = new ArrayList<>(List.of("192.168.14.100", "192.168.14.101", "192.168.14.102"));
        List<String> clusters2 = new ArrayList<>(List.of("192.168.14.100", "192.168.14.101", "192.168.14.102"));
        List<String> clusters3 = new ArrayList<>(List.of("192.168.14.100", "192.168.14.101", "192.168.14.102"));
        String localNodeName1 = "192.168.14.100";
        String localNodeName2 = "192.168.14.101";
        String localNodeName3 = "192.168.14.102";
        Node TLocalNode1 = new TLocalNode(clusters1, "192.168.14.100");
        Node TLocalNode2 = new TLocalNode(clusters2, "192.168.14.101");
        Node TLocalNode3 = new TLocalNode(clusters3, "192.168.14.102");
        // 2.将三个节点进行保存
        map.put(localNodeName1, TLocalNode1);
        map.put(localNodeName2, TLocalNode2);
        map.put(localNodeName3, TLocalNode3);
        TLocalNode1.start();
        TLocalNode1.start();
        TLocalNode1.start();
        TLocalNode1.start();
    }
}
