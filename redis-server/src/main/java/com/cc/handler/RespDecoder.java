package com.cc.handler;

import com.cc.protocal.resp.Resp;
import com.cc.utils.RespUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @program: cc-simple-redis
 * @description: Resp协议解码
 * @author: ccstar
 * @create: 2025-06-01  13:17
 **/

@Slf4j
public class RespDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //todo 粘包
        try{
            if(in.readableBytes() > 0){
                in.markReaderIndex();
            }
            if(in.readableBytes() < 4){
                return;
            }
            try{
                Resp resp = RespUtils.decodeU(in);
                if(resp != null){
                    log.info("decode resp:{}", resp);
                    out.add(resp);
                }
            }catch(Exception e){
                log.error("decode error");
                in.resetReaderIndex();
                return;
            }
        }catch(Exception e){
            log.error("decode error", e);
        }
    }
}
