package com.cc.config;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description: 统一配置类
 * @author: ccstar
 * @create: 2025-06-09  23:50
 **/
@Data
public class ZeusConfig {
    /**
     * 节点配置类
     */
    private NodeConfig nodeConfig;

    /**
     * 持久化配置类
     */
    private PersistenceConfig persistenceConfig;
}
