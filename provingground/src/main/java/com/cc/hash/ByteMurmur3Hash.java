package com.cc.hash;

import com.google.common.hash.Hashing;


public class ByteMurmur3Hash {
  public static int hash_32(byte[] bytes) {
    return Hashing.murmur3_32().hashBytes(bytes).asInt();
  }

  public static long hash_128(byte[] bytes) {
    return Hashing.murmur3_128().hashBytes(bytes).asInt();
  }

}
