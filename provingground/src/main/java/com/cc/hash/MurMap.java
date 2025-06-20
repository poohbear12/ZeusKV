package com.cc.hash;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
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
     * 当前数组长度
     */
  private int size;

  /**
   * 哈希桶
   */
  private Entry[] entries;


  public MurMap() {
    this(INITIAL_CAPACITY);
  }

  public MurMap(int capacity) {
    this.entries = new Entry[capacity];
  }

  public void put(String key, String value) {
    int index = hash(key.getBytes(StandardCharsets.UTF_8));
    Entry entry = new Entry(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8),null);
    entries[index % INITIAL_CAPACITY] = entry;
  }

  /**
   * 通过String类型的Key获取Value 返回byte[]
   * @param key
   * @return
   */
  public byte[] get(String key) {
    return null;
  }

  public void put(String key, ZeusData zeusData) {

  }



  public static void main(String[] args) {
  }


  private int hashIndex(byte[] key) {
    return key == null ? 0 : (hash(key) & 0x7FFFFFFF) & (entries.length - 1);
  }

  /**
   * hash -> murmur3 todo 扰动函数测试
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

    public Entry(byte[] key, byte[] value, Entry next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }
}