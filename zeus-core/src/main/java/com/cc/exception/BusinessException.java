package com.cc.exception;

/**
 * @program: zeus-kv
 * @description: 业务逻辑异常
 * @author: ccstar
 * @create: 2025-06-12  19:54
 **/

public class BusinessException extends KVStoreException {

  public BusinessException(String requestId, String errorCode, String message) {
    super(requestId, errorCode, message);
  }


}
