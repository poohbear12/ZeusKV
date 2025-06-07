package com.cc.persistence.aof.writer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @program: cc-simple-redis
 * @description: write接口
 * @author: ccstar
 * @create: 2025-06-07  14:30
 **/

public interface Writer {
    /**
     * 将buffer中的数据写入到缓冲区
     * @param buffer
     * @return
     */
    int write(ByteBuffer buffer);

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
