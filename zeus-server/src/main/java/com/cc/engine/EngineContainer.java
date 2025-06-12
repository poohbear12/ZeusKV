package com.cc.engine;

import com.cc.config.entity.ZeusConfig;
import com.cc.database.core.RedisCore;
import com.cc.database.core.RedisCoreImpl;
import com.cc.exception.AOFNotInitException;
import com.cc.exception.ConfigException;
import com.cc.exception.StoreNotInitException;
import com.cc.persistence.aof.AOFManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @program: zeus-kv
 * @description: 引擎容器
 * @author: ccstar
 * @create: 2025-06-12  20:35
 **/

@Slf4j
public class EngineContainer {
    /**
     * 存储核心
     */
    private static RedisCore storeCore;


    /**
     * aof管理器
     */
    private static AOFManager aofManager;

    private static volatile EngineContainer instance;


    private EngineContainer(ZeusConfig config) throws IOException {
        storeCore = new RedisCoreImpl();
        aofManager = new AOFManager(config.getAof(), storeCore);
    }

    /**
     * todo 优化 / 保证只被初始化一次
     * @param config
     */
    public static void init(ZeusConfig config) throws IOException {
        if (config != null) {
            if (instance == null) {
                synchronized (EngineContainer.class) {
                    if (instance == null) {
                        instance = new EngineContainer(config);
                        log.info("容器加载成功[Store|AOF]");
                    }
                }
            }
        } else {
            throw new ConfigException();
        }
    }

    public static RedisCore getStoreCore() {
        if (storeCore != null) {
            return storeCore;
        }
        throw new StoreNotInitException();
    }

    public static AOFManager getAOFManager() {
        if (aofManager != null) {
            return aofManager;
        }
        throw new AOFNotInitException();
    }

}
