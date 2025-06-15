package com.cc.config.entity;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-12  17:33
 **/

@Data
public class ZdbConfig {

  /**
   * 是否开启持久化
   */
  private boolean enable;
}
