package com.cc.exception;

/**
 * @program: zeus-kv
 * @description: 基础异常类
 * @author: ccstar
 * @create: 2025-06-12  19:50
 **/
public class SystemCoreException extends KVStoreException {
    public SystemCoreException(String requestId, String message) {
        super(requestId, "SYS-001", message);
    }


    public SystemCoreException() {
        super("默认1", "SYS-001", "系统异常!");
    }
}
