package com.cc.hash;

import com.google.common.hash.Hashing;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @program: zeus-kv
 * @description: 基于murmur3哈希函数
 * @author: ccstar
 * @create: 2025-06-17  01:07
 **/

public class MurMap {
    private static final int INITIAL_CAPACITY = 16384;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private Entry[] table;
    private int size;
    private int threshold;
    public MurMap() {
        table = new Entry[INITIAL_CAPACITY];
        threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    public static void main(String[] args) {
        MurMap murMap = new MurMap();
        murMap.testCollisionRate(12280, 128);
    }

    // 优化的哈希函数 - 仅保留一次高低位混合
    private int hash(byte[] key) {
        int h = Hashing.murmur3_32().hashBytes(key).asInt();
        return h ^ (h >>> 16); // 移除key.length混入，依赖Murmur3原生分布
    }

    private int indexFor(int hash, int capacity) {
        return hash & (capacity - 1);
    }

    public void put(byte[] key, byte[] value) {
        if (key == null) {
            throw new NullPointerException();
        }

        // 确保可见性
        Entry[] tab = table;
        int n = tab.length;

        if (size >= threshold) {
            resize(2 * n);
            tab = table; // 扩容后重新获取table引用
            n = tab.length;
        }

        int hash = hash(key);
        int i = indexFor(hash, n);

        for (Entry e = tab[i]; e != null; e = e.next) {
            if (e.hash == hash && Arrays.equals(e.key, key)) {
                e.value = value;
                return;
            }
        }

        tab[i] = new Entry(hash, key, value, tab[i]);
        size++;
    }

    private void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;

        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        table = newTable; // 提前更新table引用，确保transfer中可见性

        // 优化的转移逻辑
        for (int i = 0; i < oldCapacity; i++) {
            Entry e = oldTable[i];
            if (e == null) {
                continue;
            }

            // 特殊处理：如果链表只有一个元素，直接放入新表
            if (e.next == null) {
                newTable[indexFor(e.hash, newCapacity)] = e;
            } else {
                // 否则遍历链表
                Entry loHead = null, loTail = null;
                Entry hiHead = null, hiTail = null;
                Entry next;

                do {
                    next = e.next;
                    // 关键优化：利用哈希高位判断元素在新表中的位置
                    if ((e.hash & oldCapacity) == 0) {
                        if (loTail == null) {
                            loHead = e;
                        } else {
                            loTail.next = e;
                        }
                        loTail = e;
                    } else {
                        if (hiTail == null) {
                            hiHead = e;
                        } else {
                            hiTail.next = e;
                        }
                        hiTail = e;
                    }
                } while ((e = next) != null);

                if (loTail != null) {
                    loTail.next = null;
                    newTable[i] = loHead;
                }

                if (hiTail != null) {
                    hiTail.next = null;
                    newTable[i + oldCapacity] = hiHead;
                }
            }
        }

        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    // 其他方法保持不变...

    public void testCollisionRate(int testSize, int keyLength) {
        MurMap map = new MurMap();
        SecureRandom random = new SecureRandom();
        int firstCollisions = 0;
        int totalCollisions = 0;

        for (int i = 0; i < testSize; i++) {
            byte[] key = new byte[keyLength];
            random.nextBytes(key);

            // 在 put 之前获取当前表（用于首次冲突检查）
            Entry[] tabBefore = map.table;
            int nBefore = tabBefore.length;
            int hash = map.hash(key);
            int bucketBefore = map.indexFor(hash, nBefore);

            // 检查首次冲突：桶在插入前是否非空
            if (tabBefore[bucketBefore] != null) {
                firstCollisions++;
            }

            map.put(key, new byte[0]); // put 可能引起扩容

            // 在 put 之后获取最新表（用于冲突统计）
            Entry[] tabAfter = map.table;
            int nAfter = tabAfter.length;
            int bucketAfter = map.indexFor(hash, nAfter); // 基于新表计算索引

            // 统计插入后新桶的链表长度（至少包含新元素）
            int chainLength = 0;
            for (Entry e = tabAfter[bucketAfter]; e != null; e = e.next) {
                chainLength++;
            }
            totalCollisions += chainLength - 1; // 冲突数 = 链表长度 - 1（新元素自身）
        }

        double firstCollisionRate = (double) firstCollisions / testSize;
        double avgCollisionRate = (double) totalCollisions / testSize;

        System.out.printf("首次冲突率: %.6f%%%n", firstCollisionRate * 100);
        System.out.printf("平均每个元素冲突次数: %.6f%%%n", avgCollisionRate * 100);
    }

    private static class Entry {
        final byte[] key;
        final int hash;
        byte[] value;
        Entry next;

        Entry(int hash, byte[] key, byte[] value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}