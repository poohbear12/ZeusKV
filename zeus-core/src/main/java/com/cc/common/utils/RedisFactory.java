package com.cc.common.utils;

import com.cc.database.datastructure.RedisString;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-03  17:12
 **/


public class RedisFactory<T> {
  public <T> T get(Class clazz) {
    if (clazz.equals(RedisString.class)) {
      return (T) new RedisString();
    }
    return null;
  }
}
