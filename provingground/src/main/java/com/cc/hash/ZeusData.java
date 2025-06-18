package com.cc.hash;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-18  23:55
 **/


public interface ZeusData {
  ZeusData decode(byte[] bytes);
  byte[] encode();
}
