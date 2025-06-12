package com.cc.exception;

import java.util.Map;

/**
 * @program: zeus-kv
 * @description: 基础异常类
 * @author: ccstar
 * @create: 2025-06-12  19:50
 **/

public class KVStoreException extends RuntimeException {

    private final String requestId;
    private final String errorCode;

    public KVStoreException(String requestId, String errorCode, String message) {
        super(message);
        this.requestId = requestId;
        this.errorCode = errorCode;
    }

    // 获取异常上下文信息
    public Map<String, Object> getContext() {
        return Map.of(
                "requestId", requestId,
                "errorCode", errorCode,
                "timestamp", System.currentTimeMillis()
        );
    }

}
