package com.cc.config;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description: 持久化配置类
 * @author: ccstar
 * @create: 2025-06-09  23:38
 **/
@Data
public class PersistenceConfig {

    private AofConfig aofConfig;

    private ZdbConfig zdbConfig;

    @Data
    public static class AofConfig {

        /**
         * 是否开启AOF持久化
         */
        private boolean enable;

        /**
         * AOF文件名
         */
        private String filename;

        /**
         * 文件预分配大小 1 * 1024 * 1024 1MB
         */
        private int defaultPreallocateSize;

        /**
         * 刷盘间隔 单位 MS
         */
        private int flushInterval;

        /**
         * 大key大小 512 * 1024 0.5 MB
         */
        private int lagerCmdThreshold;

        /**
         * 背压阈值设置 6 * 1024 * 1024 6MB
         */
        private int defaultBackPressureThreshold;

        /**
         * 阻塞队列长度
         */
        private int defaultQueueSize;

        /**
         * 最小批次大小
         */
        private int minBatchSize;

        /**
         * 最大批次大小
         */
        private int maxBatchSize;

        /**
         * 最小批次间隔
         */
        private int minBatchTimeOutMs;

        /**
         * 最大批次间隔
         */
        private int maxBatchTimeOutMs;
    }

    @Data
    public static class ZdbConfig {
        /**
         * 是否开启ZDB持久化
         */
        private boolean enable;

    }
}
