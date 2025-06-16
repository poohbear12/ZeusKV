package com.cc.hash;

public class ByteArrayHash {
    // 31基数乘法哈希
    public static int hash31(byte[] key) {
        int hash = 0;
        for (byte b : key) {
            hash = 31 * hash + (b & 0xFF); // 处理有符号byte
        }
        return hash;
    }

    // 37基数乘法哈希
    public static int hash37(byte[] key) {
        int hash = 0;
        for (byte b : key) {
            hash = 37 * hash + (b & 0xFF);
        }
        return hash;
    }
}