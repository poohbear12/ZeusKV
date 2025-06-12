package com.cc;

import com.cc.config.loader.ConfigLoader;
import com.cc.config.check.EnvCheck;
import com.cc.config.entity.ZeusConfig;
import com.cc.engine.EngineContainer;
import com.cc.server.KVServer;
import com.cc.server.netty.NettyServer;
import com.cc.utils.BannerPrinter;
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
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try {
            start();
        } catch (IOException e) {
            log.error("IO异常!{}",e);
        }

        log.info("启动耗时 {}", System.currentTimeMillis() - start + "MS");
    }

    static void start() throws IOException {
        // 1. 加载配置文件 默认加载zeus.yaml
        ConfigLoader configLoader = new ConfigLoader();
        ZeusConfig config = configLoader.getConfig();

        // 2. 检查配置环境
        EnvCheck.check(config);

        // 3. 启动容器
        EngineContainer.init(config);

        // 4. 输出启动成功日志
        BannerPrinter.printBannerFromFile();

        // 5. Netty启动
        KVServer server = new NettyServer(config);


    }


}
