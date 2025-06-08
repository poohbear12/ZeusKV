package com.cc.protocal.resp;


import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: zeus-kv
 * @description: SimpleString
 * @author: ccstar
 * @create: 2025-06-01  12:12
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSimpleStrings extends AbstracResp{

    private String content;

    @Override
    protected Resp absDecode(ByteBuf buffer) {
        this.content = getString(buffer);;
        return this;
    }


    @Override
    protected void absEncode(ByteBuf buffer) {
        buffer.writeByte('+');
        buffer.writeBytes(this.content.getBytes());
        buffer.writeBytes(CRLF);
    }

}
