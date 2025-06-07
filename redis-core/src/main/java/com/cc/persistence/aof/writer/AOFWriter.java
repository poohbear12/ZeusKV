package com.cc.persistence.aof.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @program: cc-simple-redis
 * @description:
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/


public class AOFWriter implements Writer{

    private File file;

    /**
     * 文件输入通道
     */
    private FileChannel channel;

    /**
     * 文件访问
     */
    private RandomAccessFile raf;

    /**
     * 文件写入大小
     */
    private AtomicLong size;

    /**
     * 是否预分配内存
     */
    private boolean isPreallocated;

    /**
     * 预分配内存大小 4 * 1024 * 1024 4MB
     */
    private static final int DEFAULT_PREALLOCATE_SIZE = 4 * 1024 * 1024;

    public AOFWriter(File file, boolean isPreallocated, int flushInterval, FileChannel channel) throws FileNotFoundException {
        this.file = file;
        this.isPreallocated = isPreallocated;
        if (channel == null) {
            this.raf = new RandomAccessFile(file, "rw");
            this.channel = raf.getChannel();
            channel = this.channel;
        } else {
            this.channel = channel;
        }

        try {
            this.size = new AtomicLong(channel.size());
            if (isPreallocated) {
                this.size.set(0);
                preAllocated(DEFAULT_PREALLOCATE_SIZE);
            }
            this.channel.position(this.size.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void preAllocated(int defaultPreallocateSize) throws IOException {
        if (this.raf != null) {
            this.raf.setLength(defaultPreallocateSize);
            this.channel.position(0);
        } else if (this.channel != null) {
            this.channel.truncate(defaultPreallocateSize);
            this.channel.position(0);
        }
    }

    /**
     * 将byteBufeer写入 buffer
     * @param buffer
     * @return
     */
    @Override
    public int write(ByteBuffer buffer) {
        int writeen = writtenFullyTo(channel, buffer);
        size.addAndGet(writeen);
        return writeen;
    }

    /**
     * 完全写入
     * @param channel
     * @param buffer
     * @return
     */
    private int writtenFullyTo(FileChannel channel, ByteBuffer buffer) {
        int originalPosition = buffer.position();
        int originalLimit = buffer.limit();
        int totalBytes = buffer.remaining();
        try {
            int writeen = 0;
            while (writeen < totalBytes) {
                // 写入内核缓冲区
                // 写入内核缓冲区
                writeen += channel.write(buffer);
            }
            return writeen;
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            buffer.position(originalPosition);
            buffer.limit(originalLimit);
        }
    }

    /**
     * 强制刷盘
     * @throws IOException
     */
    @Override
    public void flush() throws IOException{
        channel.force(true);
    }

    /**
     * 关闭
     * @throws IOException
     */
    @Override
    public void close() throws IOException{
        flush();
        channel.close();
        if (raf != null) raf.close();

    }
}
