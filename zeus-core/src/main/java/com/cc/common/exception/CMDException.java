package com.cc.common.exception;

import com.cc.common.enmu.CMDExceptionEnum;

/**
 * @program: zeus-kv
 * @description: 命令解析异常
 * @author: ccstar
 * @create: 2025-06-01  12:21
 **/

public class CMDException extends RuntimeException{

    public CMDException(CMDExceptionEnum commandExceptionEnum) {
        super(commandExceptionEnum.name());
    }
}
