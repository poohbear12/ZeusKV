package com.cc.server.netty.handler;

import com.cc.protocal.resp.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: zeus-kv
 * @description: resp协议编码
 * @author: ccstar
 * @create: 2025-06-01  13:20
 **/

@Slf4j
public class RespEncoder extends MessageToByteEncoder<Resp> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Resp msg, ByteBuf out) throws Exception {
        // todo 逻辑待优化
        try{
            msg.encode(out);
        }catch(Exception e){
            log.error("encode error");
            ctx.channel().close();
        }
    }
}
