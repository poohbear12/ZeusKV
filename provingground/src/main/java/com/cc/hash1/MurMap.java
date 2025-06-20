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

  private static int MAX_CAPACITY;

  public MurMap() {
    this(INITIAL_CAPACITY);
  }

  public MurMap(int capacity) {
    this.entries = new Entry[capacity];
    MAX_CAPACITY = capacity;
    length = new AtomicInteger(0);
  }

  /**
   * todo 后续进行优化逻辑
   * @param key
   * @return
   */
  public byte[] get(byte[] key) {
    int hashCode = hash(key);
    int index = hashcodeToIndex(hashCode);
    if (entries[index] != null) {
      Entry current = entries[index];
      while (current != null) {
        if (current.getHashCode() == hashCode) {
          return current.getValue();
        }
        current = current.next;
      }
    }
    return new byte[0];
  }

  /**
   * todo 后续需要进行较大修改
   * 1. put方法逻辑流程
   * 2. 并发安全考虑
   * @param key
   * @param value
   */
  public void put(byte[] key, byte[] value) {
    int hashCode = hash(key);
    int index = hashcodeToIndex(hashCode);
    // 1. 判断当前位置是否为空
    if (entries[index] == null) {
      entries[index] = new Entry(key, value, hashCode);
      length.getAndIncrement();
    } else {
      // 2. 不为空
      Entry current = entries[index];
      // 2.1 flag判断是否修改标志位
      boolean flag = false;
      // 3. 判断链表下一个节点是否为空
      while (current.next != null) {
        // 4. 判断链表节点中hash值是否相同
        if (current.getHashCode() == hashCode) {
          current.setValue(value);
          flag = true;
          break;
        }
        current = current.next;
      }
      // 5. 如果未被修改则进入
      if (!flag) {
        if (current.getHashCode() == hashCode) {
          current.setValue(value);
        } else {
          current.setNext(new Entry(key, value, hashCode));
        }
      }
    }
  }

  /**
   * 根据hash值计算数组下标
   * @param hashCode
   * @return
   */
  private int hashcodeToIndex(int hashCode) {
    return (hashCode & 0x7FFFFFFF) & (MAX_CAPACITY - 1);
  }

  /**
   * 计算哈希值并返回数组中的位置
   * @param key
   * @return
   */
  private int hashToIndex(byte[] key) {
    return key == null ? 0 : (hash(key) & 0x7FFFFFFF) & (MAX_CAPACITY - 1);
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

    public Entry(byte[] key, byte[] value, int hashCode) {
      this.key = key;
      this.value = value;
      this.hashCode = hashCode;
    }
  }
}