package com.cc.persistence.aof.writer;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @program: cc-simple-redis
 * @description: write接口
 * @author: ccstar
 * @create: 2025-06-07  14:30
 **/

public interface Writer {

    /**
     * 写入内核缓冲区
     * @param buffer
     * @return
     * @throws IOException
     */
    int write(ByteBuf buffer) throws IOException;

    /**
     * 刷盘
     * @throws IOException
     */
    void flush() throws IOException;

    /**
     * 关闭
     * @throws IOException
     */
    void close() throws IOException;
}
