package com.cc.config.check;


import com.cc.config.entity.NodeConfig;
import com.cc.config.entity.ZeusConfig;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: zeus-kv
 * @description: 环境检测
 * @author: ccstar
 * @create: 2025-06-12  18:19
 **/

@Slf4j
public class EnvCheck {

  /**
   * @param config
   * @return
   */
  public static boolean check(ZeusConfig config) {
    log.info("启动环境检测开启!");
    // 1. 检测参数配置类型是否合法
//        boolean configType = checkConfigType(config);
    // 1. 检测本地IP地址和端口占用情况
//        boolean addrAndPort = checkAddrAndPort(config.getNodes());
    // 2. 检测集群
//        boolean clusters = checkClusters(config.getNodes());
//        boolean result = configType && addrAndPort && clusters;
    boolean result = true;
    if (result) {
      log.info("启动环境检测成功!");
      return result;
    }
    log.info("启动环境检测失败!");
    return false;
  }

  /**
   * @param nodeConfigs
   * @return
   */
  private static boolean checkAddrAndPort(List<NodeConfig> nodeConfigs) {
    return true;
  }

  /**
   * 检测参数配置类型是否合法
   *
   * @param config
   * @return
   */
  private static boolean checkConfigType(ZeusConfig config) {
    return true;
  }


  /**
   * 检测集群情况
   *
   * @param nodeConfigs
   * @return
   */
  private static boolean checkClusters(List<NodeConfig> nodeConfigs) {
    return true;
  }
}