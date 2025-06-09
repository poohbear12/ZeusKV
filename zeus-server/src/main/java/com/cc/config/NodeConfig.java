package com.cc.config;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description: 节点配置类
 * @author: ccstar
 * @create: 2025-06-09  23:33
 **/

@Data
public class NodeConfig {
    /**
     * 节点名称 zeus_one
     */
    private String name;

    /**
     * 节点IP地址 127.0.0.1
     */
    private String host;

    /**
     * 节点端口地址 6379
     */
    private int port;

    /**
     * 正常类型 normal / Master / Slave / Lost
     */
    private NodeType type;

    /**
     * 集群ID xx
     */
    private int clusterId;

    /**
     * 心跳检测间隔时间 1000ms
     */
    private int heartBeatInterval;

    /**
     * 故障检测超时时间 1000ms
     */
    private int failureDetectionTimeout;

    /**
     * 最大连接数 10
     */
    private int maxConnections;
}
