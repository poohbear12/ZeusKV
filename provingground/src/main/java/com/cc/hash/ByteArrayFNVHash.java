package com.cc.hash;

public class ByteArrayFNVHash {
    private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV1_PRIME_64 = 0x100000001b3L;

    public static long hash64(byte[] key) {
        long hash = FNV1_64_INIT;
        for (byte b : key) {
            hash ^= (b & 0xFF);
            hash *= FNV1_PRIME_64;
        }
        return hash;
    }

    // 转换为int范围的哈希值
    public static int hash32(byte[] key) {
        long hash64 = hash64(key);
        return (int)(hash64 ^ (hash64 >>> 32));
    }
}