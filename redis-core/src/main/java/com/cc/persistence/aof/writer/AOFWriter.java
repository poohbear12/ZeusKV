package com.cc.persistence.aof.writer;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: cc-simple-redis
 * @description: 负责文件写入
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/

@Getter
@Setter
public class AOFWriter implements Writer{

    private File file;

    private  FileChannel channel;

    private RandomAccessFile raf;

    /**
     * 预分配空间大小
     */
    private final int DEFAULT_PREALLOCATE_SIZE;

    public AOFWriter(File file, int DEFAULT_PREALLOCATE_SIZE) throws IOException {
        this.file = file;
        this.DEFAULT_PREALLOCATE_SIZE = DEFAULT_PREALLOCATE_SIZE;
        this.raf = new RandomAccessFile(file, "rw");
        this.channel = raf.getChannel();
        preAllocated(DEFAULT_PREALLOCATE_SIZE);
    }

    private void preAllocated(int defaultPreallocateSize) throws IOException {
        if (defaultPreallocateSize == 0) {
            return;
        }
        if (this.raf != null) {
            this.raf.setLength(defaultPreallocateSize);
            this.channel.position(0);
        } else if (this.channel != null) {
            this.channel.truncate(defaultPreallocateSize);
            this.channel.position(0);
        }
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
        if (raf != null) {
            raf.close();
        }
    }
}
