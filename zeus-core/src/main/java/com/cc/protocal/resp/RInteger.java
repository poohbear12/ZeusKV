package com.cc.protocal.resp;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: zeus-kv
 * @description: 整数类型
 * @author: ccstar
 * @create: 2025-06-01  12:12
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RInteger extends AbstracResp {

  private int content;

  @Override
  protected Resp absDecode(ByteBuf buffer) {
    this.content = getNumber(buffer);
    return this;
  }

  @Override
  protected void absEncode(ByteBuf buffer) {
    buffer.writeByte(':');
    buffer.writeBytes(String.valueOf(this.content).getBytes());
    buffer.writeBytes(CRLF);
  }
}
