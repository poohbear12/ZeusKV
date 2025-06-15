package com.cc.exception;

/**
 * @program: zeus-kv
 * @description: 配置类异常
 * @author: ccstar
 * @create: 2025-06-12  19:47
 **/


public class ConfigException extends BusinessException {


  public ConfigException(String requestId, String errorCode, String message) {
    super(requestId, errorCode, message);
  }

  public ConfigException() {
    super("默认", "er:1", "配置信息异常,请检查!");
  }

}
