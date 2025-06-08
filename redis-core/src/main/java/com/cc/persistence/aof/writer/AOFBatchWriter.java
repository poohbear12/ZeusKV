package com.cc.persistence.aof.writer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @program: cc-simple-redis
 * @description: 批量写入实现类 / 多重任务
 * @author: ccstar
 * @create: 2025-06-07  14:31
 **/

@Slf4j
public class AOFBatchWriter implements Writer{

    /**
     * 文件写入器
     */
    private final Writer aofWriter;

    /**
     * 刷盘时间
     */
    private int flushInterval;

    /**
     * 刷盘线程池
     */
    private ScheduledThreadPoolExecutor flushThread;

    /**
     * write线程
     */
    private Thread writeThread;

    /**
     * write线程是否启动
     */
    private AtomicBoolean running;

    /**
     * 阻塞队列
     */
    private BlockingDeque<ByteBuf> writeQueue;

    /**
     * 阻塞队列长度
     */
    private final int DEFAULT_QUEUE_SIZE;

    /**
     * Batch化处理参数设置
     */
    private int MIN_BATCH_SIZE;

    private int MAX_BATCH_SIZE;

    private int MIN_BATCH_TIMEOUT_MS;

    private int MAX_BATCH_TIMEOUT_MS;

    /**
     * big key大小
     */
    private int LAGER_CMD_THRESHOLD;

    /**
     * 背压参数
     */
    private int DEFAULT_BACKPRESSURE_THRESHOLD;

    private static final AtomicInteger pendingBytes = new AtomicInteger(0);

    /**
     * 是否强制刷盘
     */
    private static final AtomicBoolean forceFlush = new AtomicBoolean(false);



    public AOFBatchWriter(Writer aofWriter, int flushInterval) {
        this(aofWriter,flushInterval,2000,
                16,
                50,
                2,
                10,
                512 * 1024,
                6*1024*1024
               );
    }

    public AOFBatchWriter(Writer aofWriter, int flushInterval,
                          int DEFAULT_QUEUE_SIZE,
                          int minBatchSize,
                          int maxBatchSize,
                          int minBatchTimeoutMs,
                          int maxBatchTimeoutMs,
                          int LAGER_CMD_THRESHOLD,
                          int DEFAULT_BACKPRESSURE_THRESHOLD) {
        this.DEFAULT_QUEUE_SIZE = DEFAULT_QUEUE_SIZE;
        this.aofWriter = aofWriter;
        this.flushInterval = flushInterval;
        this.MIN_BATCH_SIZE = minBatchSize;
        this.MAX_BATCH_SIZE = maxBatchSize;
        this.MIN_BATCH_TIMEOUT_MS = minBatchTimeoutMs;
        this.MAX_BATCH_TIMEOUT_MS = maxBatchTimeoutMs;
        this.LAGER_CMD_THRESHOLD = LAGER_CMD_THRESHOLD;
        this.DEFAULT_BACKPRESSURE_THRESHOLD = DEFAULT_BACKPRESSURE_THRESHOLD;
        // 工作线程
        startWrite();
        // 刷盘线程
        startFlush();
    }

    /**
     * 启动写入
     */
    private void startWrite() {
        this.running = new AtomicBoolean(true);
        this.writeQueue = new LinkedBlockingDeque<ByteBuf>(DEFAULT_QUEUE_SIZE);
        this.writeThread = new Thread(this::handlerWrite);
        this.writeThread.setName("AOF-WRITE-Thread");
        this.writeThread.setDaemon(true);
        this.writeThread.start();
    }

    /**
     * 启动刷盘
     */
    private void startFlush() {
        this.flushThread = new ScheduledThreadPoolExecutor(1, t -> {
            Thread thread = new Thread(t);
            thread.setName("AOF-FLUSH-Thread");
            thread.setDaemon(true);
            return thread;
        });
        if (flushInterval > 0) {
            this.flushThread.scheduleAtFixedRate(() -> {
                try {
                    if (forceFlush.compareAndSet(true, false)) {
                        aofWriter.flush();
                    }
                } catch (Exception e) {
                    log.error("Failed to flush AOF file",e);
                }
            }, flushInterval, flushInterval, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * 处理写入
     */
    private void handlerWrite()  {
        int currentSize = 0;
        ByteBuf[] batch = new ByteBuf[MAX_BATCH_SIZE];
        while (running.get() || !writeQueue.isEmpty()) {
            try {
                // 计算批次
                // 计算时间
                int batchSize = calculateBatchSize();
                long deadline = System.currentTimeMillis() + calculateTimeout();
                while (currentSize < batchSize && System.currentTimeMillis() < deadline) {
                    ByteBuf item = writeQueue.poll(Math.max(1, deadline - System.currentTimeMillis()), TimeUnit.MILLISECONDS);
                    // todo 这里写入需要考虑好时间
                    if (item != null) {
                        batch[currentSize++] = item;
                    } else {
                        break;
                    }
                }
                if (currentSize > 0) {
                    writeBatch(batch, currentSize);
                    for (int i = 0; i < currentSize; i++) {
                        batch[i].release();
                        batch[i] = null;
                    }
                    currentSize = 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Failed to Handler write queue",e);
                for (int i = 0; i < currentSize; i++) {
                    batch[i].release();
                    batch[i] = null;
                }
                currentSize = 0;
            }
        }
     }




    /**
     * 刷盘
     * @param buffer
     * @return
     */
    public int write(ByteBuf buffer) throws IOException {
        int readAbleBytes = buffer.readableBytes();
        pendingBytes.addAndGet(readAbleBytes);
        if (pendingBytes.get() > DEFAULT_BACKPRESSURE_THRESHOLD || writeQueue.size() > DEFAULT_QUEUE_SIZE * 0.75) {
            applyBackpressure();
        }
        // big key
        if (readAbleBytes > LAGER_CMD_THRESHOLD) {
            try {
                aofWriter.write(buffer);
            } finally {
                buffer.release();
            }
        }
        // 背压

        try {
            boolean success = writeQueue.offer(buffer, 3, TimeUnit.SECONDS);
            if (!success) {
                aofWriter.write(buffer);
                buffer.release();
            }
            forceFlush.set(true);
        } catch (Exception e) {
            buffer.release();
            Thread.currentThread().interrupt();
        }
        if (flushInterval == 0) {
            aofWriter.flush();
        }
        return -1;
    }


    @Override
    public void flush() throws IOException {
        while (!writeQueue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            aofWriter.flush();
        }
    }


    @Override
    public void close() throws IOException {
        if (flushThread != null) {
            flushThread.shutdown();
            try {
                flushThread.shutdownNow();
                flushThread.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                flushThread.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        flush();
        running.set(false);
        writeThread.interrupt();
    }

    /**
     * 背压设置 todo 后续优化
     */
    private void applyBackpressure() {
        if(pendingBytes.get() > DEFAULT_BACKPRESSURE_THRESHOLD){
            try{
                long waitTime = Math.min(20,(pendingBytes.get() - DEFAULT_BACKPRESSURE_THRESHOLD) / 1024);
                Thread.sleep(waitTime);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * todo 后续优化
     * @return
     */
    private long calculateTimeout() {
        int queueSize = writeQueue.size();
        if(queueSize > DEFAULT_QUEUE_SIZE / 2){
            return MIN_BATCH_TIMEOUT_MS;
        }
        return MAX_BATCH_TIMEOUT_MS;
    }

    /**
     * todo 后续优化
     * @return
     */
    private int calculateBatchSize() {
        int queueSize = writeQueue.size();
        return  Math.min(MAX_BATCH_SIZE,Math.min(MIN_BATCH_SIZE, MIN_BATCH_SIZE + queueSize / 20));
    }

    private void writeBatch(ByteBuf[] batch, int batchSize) {
        try {
            CompositeByteBuf comBuffer = Unpooled.compositeBuffer();
            for (int i = 0; i < batchSize; i++) {
                comBuffer.addComponent(true,batch[i].retain());
            }
            aofWriter.write(comBuffer);
        } catch (Exception e) {
            log.error("Failed to write batch to AOF file",e);
        }
    }


}
