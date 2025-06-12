package com.cc.persistence.aof.writer;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: zeus-kv
 * @description: 负责文件写入
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/

@Getter
@Setter
public class AOFWriter implements Writer{

    private  FileChannel channel;

    public AOFWriter(FileChannel channel) {
        this.channel = channel;
    }

    /**
     * 将buffer中数据写入FileChannel
     * @param buffer
     * @return
     * @throws IOException
     */
    @Override
    public int write(ByteBuf buffer) throws IOException {
        ByteBuffer byteBuffer = buffer.nioBuffer();
        int originalPosition = byteBuffer.position();
        int originalLimit = byteBuffer.limit();
        int totalBytes = byteBuffer.remaining();
        try {
            int written = 0;
            while (written < totalBytes) {
                written += channel.write(byteBuffer);
            }
            return written;
        } finally {
            byteBuffer.position(originalPosition);
            byteBuffer.limit(originalLimit);
        }
    }

    @Override
    public void flush() throws IOException {
        channel.force(true);
    }

    @Override
    public void close() throws IOException {
        flush();
        if (channel != null) {
            channel.close();
        }
    }
}
