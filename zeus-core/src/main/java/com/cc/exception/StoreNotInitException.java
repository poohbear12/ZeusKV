package com.cc.exception;

public class StoreNotInitException extends BusinessException {
  public StoreNotInitException(String requestId, String errorCode, String message) {
    super(requestId, errorCode, message);
  }

  public StoreNotInitException() {
    super("默认", "1", "存储核心未初始化");
  }
}
