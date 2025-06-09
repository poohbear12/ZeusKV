package com.cc;

import com.cc.database.core.RedisCoreImpl;
import com.cc.server.KVServer;
import com.cc.server.zeus.ZeusKVServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @program: zeus-kv
 * @description: zeus server 启动器
 * @author: ccstar
 * @create: 2025-05-31  21:05
 **/
@Slf4j
public class ZeusKVServerLauncher
{
    public static void main(String[] args) throws IOException {
//        KVServer zeusServer = new ZeusKVServer("0.0.0.0",6379,new RedisCoreImpl());
//        zeusServer.start();
            start();
    }

    static void start() {
      printBanner();
    }

    static void printBanner () {
        StringBuilder banner = new StringBuilder();
        banner.append("\n");
        banner.append("======================================================================\n");
        banner.append("  __ _  _ __  _ __   __ _  _ __    __ _  _ __   __ _  _ __    __ _ \n");
        banner.append(" / _` || '__|| '__| / _` || '_ \\  / _` || '__| / _` || '_ \\  / _` |\n");
        banner.append("| (_| || |   | |   | (_| || | | || (_| || |   | (_| || | | || (_| |\n");
        banner.append(" \\__,_||_|   |_|    \\__,_||_| |_| \\__, ||_|    \\__,_||_| |_| \\__, |\n");
        banner.append("                                    __/ |                        __/ |\n");
        banner.append("                                   |___/                        |___/ \n");
        banner.append("======================================================================\n");
        banner.append("                          Zeus-KV v1.0.0                             \n");
        banner.append("                      High Performance KV Store                      \n");
        banner.append("======================================================================\n");

        log.info(banner.toString());
    }

}
