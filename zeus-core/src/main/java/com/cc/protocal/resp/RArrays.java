package com.cc.protocal.resp;

import com.cc.common.utils.RespUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: zeus-kv
 * @description: Arrays类型指令
 * @author: ccstar
 * @create: 2025-06-01  12:12
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RArrays extends AbstracResp{

    private Resp[] content;

    @Override
    protected Resp absDecode(ByteBuf buffer) {
        int number = getInteger(buffer);
        content = new Resp[number];
        for(int i = 0; i < number; i++){
            content[i] = RespUtils.decodeU(buffer);
        }
        return this;
    }

    @Override
    protected void absEncode(ByteBuf buffer) {
        buffer.writeByte('*');
        buffer.writeBytes(Integer.toString(content.length).getBytes());
        for(Resp r : content) {
            r.encode(buffer);
        }
        buffer.writeBytes(CRLF);
    }

}
