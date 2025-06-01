package com.cc.server;

/**
 * @program: cc-simple-redis
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
     * 服务端启动
     * @param port 启动端口号
     */
    void start(int port);

    /**
     * 服务端启动
     * @param host 启动端口号
     * @param port 启动地址
     */
    void start(String host, int port);

    /**
     * 服务端停止
     */
    void stop();
}
