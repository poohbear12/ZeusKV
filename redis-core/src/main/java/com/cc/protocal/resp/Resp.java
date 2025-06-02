package com.cc.protocal.resp;

import io.netty.buffer.ByteBuf;

/**
 * @program: cc-simple-redis
 * @description: Resp协议接口类
 * @author: ccstar
 * @create: 2025-06-01  13:45
 **/

public interface Resp {
    /**
     * 解码
     * @param buffer
     * @return
     */
    Resp decode(ByteBuf buffer);

    /**
     * 编码
     * @param buffer
     *
     */
    void encode(ByteBuf buffer);
}
