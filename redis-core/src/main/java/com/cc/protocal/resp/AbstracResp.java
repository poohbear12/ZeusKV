package com.cc.protocal.resp;

import com.cc.common.enmu.CMDExceptionEnum;
import com.cc.common.exception.CMDException;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


/**
 * @program: cc-simple-redis
 * @description: Resp协议抽象类
 * @author: ccstar
 * @create: 2025-06-01  12:09
 **/
@Slf4j
public abstract class AbstracResp implements Resp{
    /**
     * 终止符
     */
    public static final byte[] CRLF = "\r\n".getBytes();
    /**
     * 5种类型消息
     * 1. +: simple Strings "+OK\r\n"
     * 2. -: Errors "-Error message\r\n"
     * 3. :: Integer :0\r\n
     * 4. $:Bulk Strings "$6\r\nfoobar\r\n"
     * 5. *: Arrays "*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"
     * @param buffer
     * @return
     */
    /**
     * 解码 -> 子类实现
     * @param buffer
     * @return
     */
    @Override
    public Resp decode(ByteBuf buffer) {
        return absDecode(buffer);
    }

    /**
     * 编码 -> 子类实现
     * @param buffer
     */
    @Override
    public void encode(ByteBuf buffer) {
        absEncode(buffer);
    }


    protected abstract Resp absDecode(ByteBuf buffer);
    protected abstract void absEncode(ByteBuf buffer);


    /**
     * 从buffer中读取String
     * @param buffer
     * @return
     */
    protected static String getString(ByteBuf buffer){
        char c;
        StringBuilder result = new StringBuilder();
        while((c = (char)buffer.readByte()) != '\r' && buffer.readableBytes()>0){
            result.append(c);
        }
        if(buffer.readableBytes()<=0 || buffer.readByte() != '\n'){
            throw new CMDException(CMDExceptionEnum.missingTerminatorError);
        }
        return result.toString();
    }

    /**
     * 从buffer读取int
     * @param buffer
     * @return
     */
    protected static int getNumber(ByteBuf buffer){
        char c;
        c = (char)buffer.readByte();
        boolean positive = true;
        int value = 0;
        if(c == '-'){
            positive = false;
        }
        else{
            value = c - '0';
        }
        while((c = (char)buffer.readByte()) != '\r' && buffer.readableBytes()>0){
            value = value*10 + (c - '0');
        }
        if(buffer.readableBytes() <= 0 || buffer.readByte() != '\n'){
            throw new CMDException(CMDExceptionEnum.missingTerminatorError);
        }
        if(!positive){
            value = -value;
        }
        return value;
    }
}
