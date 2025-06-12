package com.cc.exception;

import com.cc.persistence.aof.AOFManager;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-12  20:46
 **/


public class AOFNotInitException extends BusinessException{
    public AOFNotInitException(String requestId, String errorCode, String message) {
        super(requestId, errorCode, message);
    }

    public AOFNotInitException() {
        super("默认","1","AOF管理器未初始化!");
    }
}
