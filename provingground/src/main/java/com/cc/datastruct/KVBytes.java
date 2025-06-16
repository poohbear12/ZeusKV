package com.cc.datastruct;

import lombok.Data;

/**
 * @program: zeus-kv
 * @description: 均采用bytes
 * @author: ccstar
 * @create: 2025-06-15  23:39
 **/
@Data
public class KVBytes {

  private byte[] key;

  private byte[] value;

  public static void main(String[] args) {
  }
}
