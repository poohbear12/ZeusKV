package com.cc.datastruct;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description: 对象类型存储数据结构 测试
 * @author: ccstar
 * @create: 2025-06-15  23:21
 **/
@Data
public class KVObject {
  private String key;

  private Object value;
}
