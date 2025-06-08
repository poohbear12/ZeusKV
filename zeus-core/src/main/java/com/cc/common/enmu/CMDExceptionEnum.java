package com.cc.common.enmu;

/**
 * @program: zeus-kv
 * @description: 指令异常枚举类
 * @author: ccstar
 * @create: 2025-06-01  12:23
 **/


public enum CMDExceptionEnum {

    /**
     * common通用异常,非特指
     */
    commandError("命令异常!",-1),

    shortError("命令过短!",1),

    typeError("非redis指令!",2),

    missingTerminatorError("命令缺少终止符!",3);




    private String name;

    private int code;

    CMDExceptionEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }
}
