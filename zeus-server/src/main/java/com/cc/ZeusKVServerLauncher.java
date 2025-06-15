package com.cc;

import com.cc.config.check.EnvCheck;
import com.cc.config.entity.ZeusConfig;
import com.cc.config.loader.ConfigLoader;
import com.cc.engine.EngineContainer;
import com.cc.server.KVServer;
import com.cc.server.netty.NettyServer;
import com.cc.utils.BannerPrinter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: zeus-kv
 * @description: zeus server 启动器
 * @author: ccstar
 * @create: 2025-05-31  21:05
 **/
@Slf4j
public class ZeusKVServerLauncher {
  public static void main(String[] args) {
    try {
      start();
    } catch (IOException e) {
      log.error("IO异常!{}", e);
    }
  }

  static void start() throws IOException {
    long start = System.currentTimeMillis();

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

    log.info("启动IP地址:{} | 启动端口号:{} | 启动时间:{}", config.getNodes().get(0).getAddr(),
        config.getNodes().get(0).getPort(), System.currentTimeMillis() - start + " MS");
  }


}
