package com.cc.common.enmu;

/**
 * @program: zeus-kv
 * @description: redis底层数据结构枚举类型
 * @author: ccstar
 * @create: 2025-06-02  18:41
 **/


public enum RedisTypeEnum {

  string(RedisEncodingTypeEnmu.raw);

  private RedisEncodingTypeEnmu encoding;

  RedisTypeEnum(RedisEncodingTypeEnmu encoding) {
    this.encoding = encoding;
  }
}
