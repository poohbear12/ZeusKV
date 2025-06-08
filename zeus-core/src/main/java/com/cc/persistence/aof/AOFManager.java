package com.cc.persistence.aof;


import com.cc.database.core.RedisCore;
import com.cc.persistence.aof.loader.AOFLoader;
import com.cc.persistence.aof.writer.AOFBatchWriter;
import com.cc.persistence.aof.writer.AOFWriter;
import com.cc.persistence.aof.writer.Writer;
import com.cc.protocal.resp.RArrays;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

/**
 * @program: zeus-kv
 * @description: AOF操作管理器
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/
@Getter
@Setter
public class AOFManager {

    /**
     * 文件名
     */
    private final String fileName;

    /**
     * 文件写入器
     */
    private final Writer aofWriter;

    /**
     * 文件批量写入器
     */
    private final AOFBatchWriter aofBatchWriter;

    /**
     * 预分配文件大小 如未设置则不分配
     */
    private static final int DEFAULT_PREALLOCATE_SIZE = 1 * 1024 * 1024;

    /**
     * 刷盘间隔 MS
     */
    private int flushInterval;

    /**
     * Redis core
     */
    private RedisCore redisCore;

    private RandomAccessFile raf;

    private FileChannel channel;


    public AOFManager(String fileName, int flushInterval, RedisCore redisCore) throws IOException {
        this.redisCore = redisCore;
        this.fileName = fileName;
        restoreAOF();
        this.flushInterval = flushInterval;
        this.aofWriter = new AOFWriter(channel);
        this.aofBatchWriter = new AOFBatchWriter(aofWriter,flushInterval);
    }

    private void restoreAOF() throws IOException {
        File file = new File(fileName);
        this.raf = new RandomAccessFile(file,"rw");
        this.channel = raf.getChannel();
        if (file.exists() && file.length() > 0) {
            // todo AOF执行恢复工作
            AOFLoader.loaderAOF(channel,redisCore);
        } else {
            // 预分配
            preallocate();
        }
    }

    private void preallocate() throws IOException {
        if (this.raf != null) {
            this.raf.setLength(DEFAULT_PREALLOCATE_SIZE);
            this.channel.position(0);
        }
    }


    public AOFManager(String fileName,int flushInterval) throws IOException {
        this.fileName = fileName;
        restoreAOF();
        this.flushInterval = flushInterval;
        this.aofWriter = new AOFWriter(channel);
        this.aofBatchWriter = new AOFBatchWriter(aofWriter,flushInterval);
    }

    /**
     * 将指令追加进入AOF日志
     * @param cmd
     * @throws IOException
     */
    public void append(RArrays cmd) throws IOException {
        ByteBuf buffer = Unpooled.buffer();
        cmd.encode(buffer);
        aofBatchWriter.write(buffer);
    }

    /**
     * 关闭
     * @throws IOException
     */
    public void close() throws IOException {
        if (aofBatchWriter != null) {
            aofBatchWriter.close();
        }
        if (aofBatchWriter != null) {
            aofBatchWriter.close();
        }
    }

    /**
     * 启动
     * @throws IOException
     */
    public void flush() throws IOException {
        if (aofBatchWriter != null) {
            aofBatchWriter.flush();
        }
    }



}
