package com.cc.config.entity;

/**
 * @program: zeus-kv
 * @description: 节点类型
 * @author: ccstar
 * @create: 2025-06-09  23:35
 **/


public enum NodeType {
    /**
     * 普通节点 单机模式独有
     */
    SINGLE,

    /**
     * 主节点 集群模式
     */
    MASTER,

    /**
     * 从节点 集群模式
     */
    SLAVE,

    /**
     * 遗失节点 集群模式
     */
    LOST;

}
