package com.cc.hash1;

import com.google.common.hash.Hashing;
import java.util.Arrays;
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
  private static final int INITIAL_CAPACITY = 16;

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
  private Entry[] entries;

  /**
   * 当前数组中元素个数
   */
  private final AtomicInteger size;


  /**
   * 存储压力 todo 后续编写计算逻辑
   */
  private static float StoragePressure;

//  /**
//   * Entry池
//   */
//  private final ConcurrentLinkedQueue<Entry> entryPool = new ConcurrentLinkedQueue<>();
//
//  /**
//   * 从Entry池获取Entry
//   * @param key
//   * @param value
//   * @param hashCode
//   * @return
//   */
//  private Entry getEntry(byte[] key, byte[] value, int hashCode) {
//    Entry entry = entryPool.poll();
//    if (entry == null) {
//      return new Entry(key, value, hashCode);
//    }
//    entry.setKey(key);
//    entry.setValue(value);
//    entry.setHashCode(hashCode);
//    entry.setNext(null);
//    return entry;
//  }
//
//  /**
//   * 回收Entry到对象池
//   * @param entry
//   */
//  private void recycleEntry(Entry entry) {
//    entry.setKey(null);
//    entry.setValue(null);
//    entry.setNext(null);
//    entryPool.offer(entry);
//  }

  public MurMap() {
    this(INITIAL_CAPACITY);
  }

  /**
   * 指定容量大小构造函数
   * @param capacity 容量
   */
  public MurMap(int capacity) {
    this.entries = new Entry[capacity];
    size = new AtomicInteger(0);
  }

  /**
   * todo 后续进行优化逻辑 Arrays.equals 对比 1. 摘要算法 2. 布隆过滤器
   * @param key
   * @return
   */
  public byte[] get(byte[] key) {
    int hashCode = hash(key);
    int index = hashcodeToIndex(hashCode);
    if (entries[index] != null) {
      Entry current = entries[index];
      while (current != null) {
        if (current.getHashCode() == hashCode && Arrays.equals(current.getKey(), key)) {
          return current.getValue();
        }
        current = current.next;
      }
    }
    return null;
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
      size.getAndIncrement();
    } else {
      // 2. 不为空，遍历链表查找或插入
      Entry current = entries[index];
      Entry prev = null;

      while (current != null) {
        // 检查哈希值和键内容是否匹配
        if (current.getHashCode() == hashCode && Arrays.equals(current.getKey(), key)) {
          // 找到匹配的键，更新值
          current.setValue(value);
          return;
        }

        prev = current;
        current = current.getNext();
      }

      // 3. 未找到匹配的键，插入新节点（尾插法）
      prev.setNext(new Entry(key, value, hashCode));
      size.getAndIncrement();
    }
    // 判断是否需要扩容
    if (isNeedResize()) {
      resize();
    }
  }

  private boolean isNeedResize() {
    float currentLoadFactory = (size.getAcquire() / (float) entries.length);
    if (currentLoadFactory > LOAD_FACTOR) {
      return true;
    }
    return false;
  }

  /**
   * todo 后续需要进行修改
   * 删除键
   * @param key
   */
  public void remove(byte[] key) {
    int hashCode = hash(key);
    int index = hashcodeToIndex(hashCode);

    // 获取头节点并检查是否为空
    Entry current = entries[index];
    Entry prev = null;

    // 遍历链表查找匹配的键
    while (current != null) {
      // 同时比较哈希值和键内容，避免哈希冲突导致的误删
      if (current.getHashCode() == hashCode && Arrays.equals(current.getKey(), key)) {
        // 找到匹配节点，执行删除
        if (prev == null) {
          // 删除头节点
          entries[index] = current.getNext();
        } else {
          // 删除中间/尾部节点
          prev.setNext(current.getNext());
        }
        // 可在此处添加对象池回收逻辑
        size.decrementAndGet();
        return;
      }

      // 移动指针
      prev = current;
      current = current.getNext();
    }
  }


  /**
   * 扩容方法
   */
  private void resize() {
    // 获取当前数组长度
    int oldCapacity = entries.length;
    // 检查是否已达到最大容量
    if (oldCapacity >= MAXIMUM_CAPACITY) {
      // todo 双数组存储 后续优化
      throw new RuntimeException("MurMap容量超出最大值！");
    }

    // 计算新容量
    int newCapacity = oldCapacity << 1;
    // 创建新数组
    Entry[] newEntries = new Entry[newCapacity];

    // 迁移所有元素到新数组
    transfer(newEntries);

    // 替换旧数组为新数组
    entries = newEntries;
  }

  /**
   * todo 后续优化
   * 数组扩容迁移
   * @param newEntries 新的哈希桶数组
   */
  private void transfer(Entry[] newEntries) {
    int newCapacity = newEntries.length;
    // 遍历旧数组
    for (Entry oldEntry : entries) {
      if (oldEntry != null) {
        // 释放链表头节点的引用（帮助GC）
        Entry next;
        do {
          next = oldEntry.next;

          // 重新计算在新数组中的位置
          int newIndex = (oldEntry.getHashCode() & 0x7FFFFFFF) & (newCapacity - 1);

          // 使用头插法将节点插入新数组
          oldEntry.setNext(newEntries[newIndex]);
          newEntries[newIndex] = oldEntry;

          // 处理下一个节点
          oldEntry = next;
        } while (next != null);
      }
    }
  }


  /**
   * 根据hash值计算数组下标
   * @param hashCode
   * @return
   */
  private int hashcodeToIndex(int hashCode) {
    return (hashCode & 0x7FFFFFFF) & (entries.length - 1);
  }

  /**
   * 计算哈希值并返回数组中的位置
   * @param key 键
   * @return 数组下标
   */
  private int hashToIndex(byte[] key) {
    return hashcodeToIndex(hash(key));
  }

  /**
   * hash -> murmur3 todo 扰动函数测试
   *
   * @param bytes
   * @return
   */
  private int hash(byte[] bytes) {
    return bytes == null ? 0 : Hashing.murmur3_32().hashBytes(bytes).asInt();
  }

  /**
   * 返回当前Map存储元素个数
   * @return
   */
  public int currentSize() {
    return size.get();
  }

  /**
   * 返回Entries长度，哈希数组长度
   * @return
   */
  public int size() {
    return entries.length;
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
    private byte[] key;

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

    /**
     * 填充字段
     */
//    private long p1, p2, p3, p4;

    public Entry(byte[] key, byte[] value, int hashCode) {
      this.key = key;
      this.value = value;
      this.hashCode = hashCode;
    }
  }
}