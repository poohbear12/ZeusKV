package com.cc.hash1;

import com.google.common.hash.Hashing;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: zeus-kv
 * @description: 基于Murmur3 哈希表
 * @author: ccstar
 * @create: 2025-06-17  01:07
 **/

public class MurMap {
  /**
   * 初试数组大小
   */
  private static final int INITIAL_CAPACITY = 16384;

  /**
   * 负载因子
   */
  private static final float LOAD_FACTOR = 0.75f;

  /**
   * 最大容量
   */
  private static final int MAXIMUM_CAPACITY = 1 << 30;
  /**
   * 哈希桶
   */
  private final Entry[] entries;

  /**
   * 当前数组长度
   */
  private AtomicInteger length;


  public MurMap() {
    this(INITIAL_CAPACITY);
  }

  public MurMap(int capacity) {
    this.entries = new Entry[capacity];
    length = new AtomicInteger(0);
  }

//  /**
//   * 通过String类型的Key获取Value 返回byte[]
//   *
//   * @param key
//   * @return
//   */
//  public byte[] get(String key) {
//    byte[] keyb = encode(key, String.class);
//    int index = hashIndex(keyb);
//    return entries[index].getValue();
//  }
//
//  public void put(String key, ZeusData zeusData) {
//    byte[] keyb = encode(key, String.class);
//    int index = hashIndex(keyb);
//    entries[index] = new Entry(keyb, encode(zeusData, ZeusData.class));
//  }


  /**
   * todo 后续需要进行较大修改
   * 1. put方法逻辑流程
   * 2. 并发安全考虑
   * @param key
   * @param value
   */
  public void put(byte[] key, byte[] value) {
    int index = hashIndex(key);
    entries[index] = new Entry(key, value);
    length.getAndIncrement();
  }

  /**
   * todo 后续进行优化逻辑
   * @param key
   * @return
   */
  public byte[] get(byte[] key) {
    int index = hashIndex(key);
    return entries[index].getValue();
  }

  /**
   * 计算哈希值并返回数组中的位置
   * @param key
   * @return
   */
  private int hashIndex(byte[] key) {
    return key == null ? 0 : (hash(key) & 0x7FFFFFFF) & (entries.length - 1);
  }

  /**
   * hash -> murmur3 todo 扰动函数测试
   *
   * @param bytes
   * @return
   */
  private int hash(byte[] bytes) {
    return Hashing.murmur3_32().hashBytes(bytes).asInt();
  }


  /**
   * 哈希表节点（内部类）
   */
  @Setter
  @Getter
  private static class Entry {
    /**
     * Key
     */
    private final byte[] key;

    /**
     * value
     */
    private byte[] value;

    /**
     * 缓存哈希值
     */
    private int hashCode;

    /**
     * 链表下一个Entry
     */
    private Entry next;

    public Entry(byte[] key, byte[] value) {
      this.key = key;
      this.value = value;
    }
  }
}