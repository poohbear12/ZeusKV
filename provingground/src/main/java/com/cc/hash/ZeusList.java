package com.cc.hash;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-18  23:55
 **/


public class ZeusList implements ZeusData{

  @Override
  public ZeusData decode(byte[] bytes) {
    // byte
    return new ZeusList();
  }

  @Override

  public byte[] encode() {
    return new byte[0];
  }
}
