package com.cc.database.core;


import com.cc.database.datastructure.RedisObject;
import com.cc.database.datastructure.RedisString;
import java.util.Set;

/**
 * @program: zeus-kv
 * @description: Redis存储核心 -> 保存RedisDb
 * @author: ccstar
 * @create: 2025-06-01  16:08
 **/

public interface RedisCore {
  /**
   * 查看当前数据库个数
   *
   * @return
   */
  int size();

  /**
   * 切换数据库
   *
   * @param idx
   */
  void selectDb(int idx);

  /**
   * 查看当前使用数据库
   *
   * @return
   */
  int currentDb();

  /**
   * 存入一对键值对(key,value)
   *
   * @param key
   * @param value
   */
  void put(RedisString key, RedisObject value);

  /**
   * 根据Key获取value
   *
   * @param key
   * @return
   */
  RedisObject get(RedisString key);

  /**
   * 获取所有键值
   *
   * @return
   */
  Set<RedisString> keys();

  /**
   * 清空所有数据库
   */
  void flushAll();

  /**
   * 清空当前数据库
   */
  void flush();


  boolean exists(String key);

  int keyCount();

}
