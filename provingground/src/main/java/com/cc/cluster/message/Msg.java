package com.cc.cluster.message;

import lombok.Data;

/**
 * @program: ZeusKV
 * @description: 消息实体类
 * @author: ccstar
 * @create: 2025-06-25  12:16
 **/

@Data
public abstract class Msg {
    /**
     * 任期号
     */
    private int term;

    /**
     * 消息内容 Test采用String
     */
    private String content;

    /**
     * 消息目的地址
     */
    private String destinationAddr;

    /**
     * 消息来源地址
     */
    private String sourceAddr;
}
