package com.cc;

import com.cc.database.core.RedisCoreImpl;
import com.cc.server.KVServer;
import com.cc.server.zeus.ZeusKVServer;

import java.io.IOException;

/**
 * @program: zeus-kv
 * @description: zeus server 启动器
 * @author: ccstar
 * @create: 2025-05-31  21:05
 **/
public class ZeusKVServerLauncher
{
    public static void main(String[] args) throws IOException {
        KVServer zeusServer = new ZeusKVServer("0.0.0.0",6379,new RedisCoreImpl());
        zeusServer.start();
    }
}
