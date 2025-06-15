package com.cc.common.utils;


import com.cc.common.enmu.CMDExceptionEnum;
import com.cc.common.exception.CMDException;
import com.cc.protocal.resp.RArrays;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.RInteger;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;
import io.netty.buffer.ByteBuf;

/**
 * @program: zeus-kv
 * @description: Resp工具类
 * @author: ccstar
 * @create: 2025-06-01  12:41
 **/

public class RespUtils {

  // todo 粘包待解决
  public static Resp decodeU(ByteBuf buffer) {
    if (buffer.readableBytes() <= 0) {
      throw new CMDException(CMDExceptionEnum.shortError);
    }
    return handlerBytes(buffer);
  }

  /**
   * 5种类型消息
   * 1. +: simple Strings "+OK\r\n"
   * 2. -: Errors "-Error message\r\n"
   * 3. :: Integer :0\r\n
   * 4. $:Bulk Strings "$6\r\nfoobar\r\n"
   * 5. *: Arrays "*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"
   **/
  private static Resp handlerBytes(ByteBuf buffer) {
    char flag = (char) buffer.readByte();
    switch (flag) {
      case '+':
        return new RSimpleStrings().decode(buffer);
      case '-':
        return new RErrors().decode(buffer);
      case ':':
        return new RInteger().decode(buffer);
      case '$':
        return new RBulkStrings().decode(buffer);
      case '*':
        return new RArrays().decode(buffer);
      case '\u0000':
        return null;
      default:
        throw new CMDException(CMDExceptionEnum.typeError);
    }
  }


}
