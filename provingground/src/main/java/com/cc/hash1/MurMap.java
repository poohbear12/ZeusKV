package com.cc.hash1;

import com.google.common.hash.Hashing;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;
import lombok.Setter;

public class MurMap {
  /**
   * 初始数组大小（必须为2的幂）
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
   * 哈希桶数组（volatile保证可见性）
   */
  private volatile Entry[] table;

  /**
   * 扩容时的旧哈希桶数组（对应ConcurrentHashMap的nextTable）
   */
  private volatile Entry[] nextTable;

  /**
   * 当前元素个数（原子类保证并发计数）
   */
  private final AtomicInteger size = new AtomicInteger(0);

  /**
   * 扩容控制变量（核心！对应ConcurrentHashMap的sizeCtl）
   * - 正数：下次扩容的阈值（容量×负载因子）
   * - 0：默认值，初始化时使用
   * - -1：正在初始化
   * - -N：正在扩容，有N-1个线程正在协助迁移（N≥2）
   */
  private volatile int sizeCtl = 0;

  /**
   * 迁移索引（从旧数组尾部向头部迁移，避免线程竞争）
   */
  private volatile int transferIndex;

  /**
   * 桶级别读写锁数组（保留原锁机制，保证桶操作安全性）
   */
  private ReentrantReadWriteLock[] locks;

  public MurMap() {
    this(INITIAL_CAPACITY);
  }

  public MurMap(int capacity) {
    // 初始化容量调整为2的幂（同ConcurrentHashMap）
    int initCapacity = tableSizeFor(capacity);
    sizeCtl = initCapacity; // 初始时sizeCtl为容量（后续会更新为阈值）
    this.table = new Entry[initCapacity];
    this.locks = new ReentrantReadWriteLock[initCapacity];
    for (int i = 0; i < initCapacity; i++) {
      locks[i] = new ReentrantReadWriteLock();
    }
  }

  /**
   * 计算大于等于capacity的最小2的幂（同ConcurrentHashMap）
   */
  private static int tableSizeFor(int capacity) {
    int n = capacity - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
  }

  /**
   * get操作：读取时检测扩容并协助迁移
   */
  public byte[] get(byte[] key) {
    int hash = hash(key);
    // 双重检查：先读table，若有值直接返回；若正在扩容，检查nextTable
    for (Entry[] tab = table; tab != null; ) {
      Entry e;
      int n = tab.length;
      int index = hash & (n - 1); // 计算索引（同ConcurrentHashMap）
      // 读锁保护桶读取
      locks[index].readLock().lock();
      try {
        // 检查当前桶是否已迁移（若tab是旧数组，可能已被迁移）
        if (tab == table || (tab = nextTable) == null) {
          e = tab[index];
          // 遍历链表查找
          while (e != null) {
            if (e.hashCode == hash && Arrays.equals(e.key, key)) {
              return e.value;
            }
            e = e.next;
          }
        }
      } finally {
        locks[index].readLock().unlock();
      }
      // 若正在扩容，协助迁移后重试（可能table已更新）
      if (nextTable != null) {
        helpTransfer(tab);
      } else {
        break;
      }
    }
    return null;
  }

  /**
   * put操作：写入时检测扩容并触发/协助迁移
   */
  public void put(byte[] key, byte[] value) {
    Entry[] tab;
    int n, i, hash = hash(key);
    // 若table未初始化，先初始化
    if ((tab = table) == null || (n = tab.length) == 0) {
      tab = initTable();
      n = tab.length;
    }

    Entry e;
    int index = hash & (n - 1);
    // 写锁保护桶写入
    locks[index].writeLock().lock();
    try {
      // 检查当前桶是否存在该key
      if ((e = tab[index]) == null) {
        // 桶为空，直接插入新节点
        tab[index] = new Entry(key, value, hash);
      } else {
        // 桶非空，遍历链表查找
        Entry prev = null;
        while (e != null) {
          if (e.hashCode == hash && Arrays.equals(e.key, key)) {
            // 覆盖已有值
            e.value = value;
            return;
          }
          prev = e;
          e = e.next;
        }
        // 链表末尾插入新节点
        prev.next = new Entry(key, value, hash);
      }
      // 更新元素数量
      if (size.incrementAndGet() > sizeCtl) {
        // 超过阈值，触发扩容
        transfer();
      }
    } finally {
      locks[index].writeLock().unlock();
    }
  }

  /**
   * 初始化table（同ConcurrentHashMap的initTable）
   */
  private Entry[] initTable() {
    Entry[] tab;
    int sc;
    // 循环检测，确保只有一个线程初始化
    while ((tab = table) == null || tab.length == 0) {
      // 若有线程正在初始化，当前线程让出CPU
      if ((sc = sizeCtl) < 0) {
        Thread.yield();
      }
      // CAS将sizeCtl设为-1（标记正在初始化）
      else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
        try {
          if (table == null || table.length == 0) {
            // 初始化table和锁数组
            int capacity = sc > 0 ? sc : INITIAL_CAPACITY;
            tab = new Entry[capacity];
            locks = new ReentrantReadWriteLock[capacity];
            for (int i = 0; i < capacity; i++) {
              locks[i] = new ReentrantReadWriteLock();
            }
            table = tab;
            // 计算阈值（容量×负载因子）
            sc = (int) (capacity * LOAD_FACTOR);
            sizeCtl = sc;
          }
        } finally {
          // 初始化完成，恢复sizeCtl为阈值
          if (sc < 0) {
            sizeCtl = sc;
          }
        }
        break;
      }
    }
    return tab;
  }

  /**
   * 触发扩容（同ConcurrentHashMap的transfer）
   */
  private void transfer() {
    Entry[] tab = table;
    int n;
    // 若table未初始化或正在扩容，直接返回
    if (tab == null || (n = tab.length) == 0 || tab != table) {
      return;
    }

    // 检查是否需要扩容（元素数超过阈值）
    int threshold = sizeCtl;
    if (size.get() <= threshold) {
      return;
    }

    // 新容量为旧容量的2倍（必须为2的幂）
    int newCapacity = n << 1;
    if (newCapacity > MAXIMUM_CAPACITY) {
      throw new RuntimeException("MurMap容量超出最大值！");
    }

    // 创建新数组（nextTable）
    Entry[] nextTab = new Entry[newCapacity];
    nextTable = nextTab;
    // 初始化迁移索引（从旧数组尾部开始）
    transferIndex = n;

    // 启动迁移（当前线程先迁移一部分）
    if (nextTab != null && tab == table) {
      int i = transferIndex;
      if (i > 0) {
        Entry[] oldTab = tab;
        int oldCap = oldTab.length;
        Entry e;
        // 迁移索引减1（从尾部向头部迁移）
        if (U.compareAndSwapInt(this, TRANSFERINDEX, i, i - 1)) {
          // 锁定当前桶，开始迁移
          locks[i - 1].writeLock().lock();
          try {
            if (oldTab == table && (e = oldTab[i - 1]) != null) {
              // 拆分旧桶元素到新桶（高低位拆分）
              transferBucket(e, nextTab, oldCap);
              // 迁移完成，旧桶置空
              oldTab[i - 1] = null;
            }
          } finally {
            locks[i - 1].writeLock().unlock();
          }
        }
      }

      // 若所有桶迁移完成，更新table为新数组
      if (transferIndex == 0) {
        nextTable = null;
        table = nextTab;
        // 更新阈值为新容量×负载因子
        sizeCtl = (int) (newCapacity * LOAD_FACTOR);
      }
    }
  }

  /**
   * 迁移单个桶的元素到新数组（核心！高低位拆分）
   * 原理：旧容量为n，新容量为2n，元素哈希值的第(log2(n))位决定新索引：
   * - 该位为0：新索引 = 旧索引
   * - 该位为1：新索引 = 旧索引 + n
   */
  private void transferBucket(Entry head, Entry[] newTab, int oldCap) {
    Entry loHead = null, loTail = null; // 低位链（新索引=旧索引）
    Entry hiHead = null, hiTail = null; // 高位链（新索引=旧索引+oldCap）
    Entry e;

    // 拆分旧桶链表为高低位链
    do {
      e = head.next;
      // 判断哈希值的高位bit（旧容量的最高位）
      if ((head.hashCode & oldCap) == 0) {
        // 高位为0，加入低位链
        if (loTail == null) {
          loHead = head;
        } else {
          loTail.next = head;
        }
        loTail = head;
      } else {
        // 高位为1，加入高位链
        if (hiTail == null) {
          hiHead = head;
        } else {
          hiTail.next = head;
        }
        hiTail = head;
      }
      head.next = null; // 断开旧链接
    } while ((head = e) != null);

    // 将低位链放入新数组的旧索引位置
    if (loTail != null) {
      int loIndex = loHead.hashCode & (newTab.length - 1);
      newTab[loIndex] = loHead;
    }
    // 将高位链放入新数组的旧索引+oldCap位置
    if (hiTail != null) {
      int hiIndex = (hiHead.hashCode & (newTab.length - 1)) + oldCap;
      newTab[hiIndex] = hiHead;
    }
  }

  /**
   * 协助扩容（其他线程检测到扩容时协助迁移）
   */
  private void helpTransfer(Entry[] oldTab) {
    if (oldTab != null && oldTab == table && nextTable != null) {
      int i = transferIndex;
      // 若还有未迁移的桶，协助迁移
      if (i > 0 && i <= oldTab.length) {
        Entry[] nextTab = nextTable;
        if (U.compareAndSwapInt(this, TRANSFERINDEX, i, i - 1)) {
          // 锁定当前桶，协助迁移
          locks[i - 1].writeLock().lock();
          try {
            if (oldTab == table && oldTab[i - 1] != null) {
              transferBucket(oldTab[i - 1], nextTab, oldTab.length);
              oldTab[i - 1] = null; // 迁移完成，旧桶置空
            }
          } finally {
            locks[i - 1].writeLock().unlock();
          }

          // 若所有桶迁移完成，更新table
          if (transferIndex == 0) {
            nextTable = null;
            table = nextTab;
            sizeCtl = (int) (nextTab.length * LOAD_FACTOR);
          }
        }
      }
    }
  }



  /**
   * 工具方法：计算哈希（保留原Murmur3算法）
   */
  private int hash(byte[] bytes) {
    return bytes == null ? 0 : Hashing.murmur3_32().hashBytes(bytes).asInt();
  }

  /**
   * 工具方法：调整容量为2的幂
   */


  /**
   * 内部Entry类（保留原结构）
   */
  @Setter
  @Getter
  private static class Entry {
    private byte[] key;
    private byte[] value;
    private int hashCode;
    private Entry next;

    public Entry(byte[] key, byte[] value, int hashCode) {
      this.key = key;
      this.value = value;
      this.hashCode = hashCode;
    }
  }

  // 用于Unsafe操作的偏移量（模拟ConcurrentHashMap的CAS控制）
  private static final sun.misc.Unsafe U;
  private static final long SIZECTL;
  private static final long TRANSFERINDEX;

  static {
    try {
      java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      U = (sun.misc.Unsafe) f.get(null);
      SIZECTL = U.objectFieldOffset(MurMap.class.getDeclaredField("sizeCtl"));
      TRANSFERINDEX = U.objectFieldOffset(MurMap.class.getDeclaredField("transferIndex"));
    } catch (Exception e) {
      throw new Error(e);
    }
  }
}