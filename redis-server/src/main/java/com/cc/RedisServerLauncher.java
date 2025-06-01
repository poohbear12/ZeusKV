package com.cc;

import com.cc.server.KVServer;
import com.cc.server.redis.RedisServer;

/**
 * @program: cc-simple-redis
 * @description: Redis server 启动器
 * @author: ccstar
 * @create: 2025-05-31  21:05
 **/
public class RedisServerLauncher
{
    public static void main(String[] args) {
        // 监听所有访问本机端口6379连接
        KVServer redisServer = new RedisServer("0.0.0.0",6379);
        redisServer.start();
    }
}
