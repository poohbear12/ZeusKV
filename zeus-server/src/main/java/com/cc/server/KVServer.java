package com.cc.server;

/**
 * @program: zeus-kv
 * @description: KvServer 服务端接口
 * @author: ccstar
 * @create: 2025-06-01  09:57
 **/

public interface KVServer {

    /**
     * 服务端启动
     */
    void start();

    /**
     * 服务端停止
     */
    void stop();
}
