package com.cc.persistence.aof;


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
import java.nio.file.Files;

/**
 * @program: cc-simple-redis
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


    public AOFManager(String fileName,int flushInterval) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            // todo AOF执行恢复工作
        }
        this.fileName = fileName;
        this.flushInterval = flushInterval;
        this.aofWriter = new AOFWriter(file, DEFAULT_PREALLOCATE_SIZE);
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
