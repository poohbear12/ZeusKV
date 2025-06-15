package com.cc.config.entity;

import lombok.Data;

@Data
public class AofConfig {

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
  private int preallocateSize;

  /**
   * 刷盘间隔 单位 MS
   */
  private int flushInterval;

  /**
   * 大key大小 512 * 1024 0.5 MB
   */
  private int largeCmdThreshold;

  /**
   * 背压阈值设置 6 * 1024 * 1024 6MB
   */
  private int backpressureThreshold;

  /**
   * 阻塞队列长度
   */
  private int queueSize;

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
  private int minBatchTimeout;

  /**
   * 最大批次间隔
   */
  private int maxBatchTimeout;
}