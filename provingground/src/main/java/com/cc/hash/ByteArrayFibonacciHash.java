package com.cc.hash;

public class ByteArrayFibonacciHash {
    private static final long FIBONACCI_CONSTANT = 0x9e3779b97f4a7c15L; // 2^64 * φ

    public static int hash(byte[] key, int tableSize) {
        // 使用Murmur3的部分逻辑计算初始哈希
        int h = 0;
        for (int i = 0; i < key.length; i++) {
            h ^= key[i] & 0xFF;
            h *= 0x5bd1e995;
            h ^= h >>> 13;
        }
        
        // 斐波那契映射
        long hash64 = ((long)h) * FIBONACCI_CONSTANT;
        return (int)((hash64 >>> 32) & (tableSize - 1));
    }
}