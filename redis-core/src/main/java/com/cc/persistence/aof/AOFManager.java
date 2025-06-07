package com.cc.persistence.aof;

import com.cc.persistence.aof.writer.AOFBatchWriter;
import com.cc.persistence.aof.writer.AOFWriter;
import com.cc.persistence.aof.writer.Writer;
import com.cc.protocal.resp.RArrays;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @program: cc-simple-redis
 * @description: AOF管理器
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/


public class AOFManager {

    /**
     * 文件写入
     */
    private Writer aofWriter;

    /**
     * 文件批量写入
     */
    private AOFBatchWriter batchWriter;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件是否存在
     */
    private boolean fileExists;

    /**
     * 文件刷盘间隔 MS
     */
    private static final int DEFAULT_FLUSH_INTERVAL_MS = 1000;

    /**
     * 是否预分配
     */
    private static final boolean DEFAULT_PREALLOCATE = true;


    public AOFManager(String fileName) throws FileNotFoundException {
        this(fileName, new File(fileName).exists(), DEFAULT_PREALLOCATE,DEFAULT_FLUSH_INTERVAL_MS);
    }

    public AOFManager(String fileName, boolean fileExists, boolean preallocated, int flushInterval) throws FileNotFoundException {
        this.fileName = fileName;
        this.fileExists = fileExists;
        this.aofWriter = new AOFWriter(new File(fileName), preallocated, flushInterval, null);
        this.batchWriter = new AOFBatchWriter(aofWriter, flushInterval);
    }

    /**
     * 追加
     * @param rArrays
     * @throws IOException
     */
    public void append(RArrays rArrays) throws IOException {
        ByteBuf buffer = Unpooled.buffer();
        rArrays.encode(buffer);
        batchWriter.write(buffer);
    }

    /**
     * 关闭
     * @throws Exception
     */
    public void close() throws Exception {
        if (batchWriter != null) {
            batchWriter.close();
        }
        if (aofWriter != null) {
            aofWriter.close();
        }
    }

    /**
     * 刷盘
     * @throws Exception
     */
    public void flush() throws Exception {
        if (batchWriter != null) {
            batchWriter.flush();
        }
    }


}
