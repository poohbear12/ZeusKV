package com.cc.protocal.resp;


import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @program: cc-simple-redis
 * @description: BulkString 多字符串
 * @author: ccstar
 * @create: 2025-06-01  12:12
 **/

@Data
@NoArgsConstructor
public class RBulkStrings extends AbstracResp {

    private static final byte[] NULL_BYTES = "-1\r\n".getBytes();

    private static final byte[] EMPTY_BYTES = "0\r\n\r\n".getBytes();

    private byte[] content;
    
    @Override
    protected Resp absDecode(ByteBuf buffer) {
        int length = getNumber(buffer);
        if(buffer.readableBytes() < length + 2){
            throw new IllegalStateException("没有找到换行符");
        }
        byte[] content;
        if(length == -1){
            content = null;
        }else{
            content = new byte[length];
            buffer.readBytes(content);
        }
        if(buffer.readByte() != '\r' || buffer.readByte() != '\n'){
            throw new IllegalStateException("没有找到换行符");
        }
        this.content = content;
        return this;
    }

    @Override
    protected void absEncode(ByteBuf buffer) {
        buffer.writeByte('$');
        if(content == null){
            buffer.writeBytes(NULL_BYTES);
        }
        else{
            int length = content.length;
            if(length == 0){
                buffer.writeBytes(EMPTY_BYTES);
            }
            else{
                buffer.writeBytes(String.valueOf(length).getBytes());
                buffer.writeBytes(content);
                buffer.writeBytes(CRLF);
            }
        }
    }

    public String getString(){
        return new String(content, StandardCharsets.UTF_8);
    }
    public RBulkStrings(byte[] content) {
        this.content = content == null ? null : content;
    }
}
