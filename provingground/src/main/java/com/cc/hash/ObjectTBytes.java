package com.cc.hash;

import java.nio.charset.StandardCharsets;

/**
 * @program: zeus-kv
 * @description: 提供各种对象 < - > byte[] 转换方法
 * @author: ccstar
 * @create: 2025-06-20  20:59
 **/


public class ObjectTBytes {

  /**
   * Byte[] -> String
   *
   * @param str
   * @return byte[]
   */
  public static byte[] stringTByte(String str) {
    if (str == null) {
      throw new RuntimeException("传入参数不能为空！" + "ObjectTOBYtes#stringTByte");
    }
    return str.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * String -> Byte[]
   *
   * @param bytes
   * @return String
   */
  public static String byteTString(byte[] bytes) {
    if (bytes == null) {
      throw new RuntimeException("传入参数不能为空！" + "ObjectTBytes#byteTString");
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
