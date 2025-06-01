package com.cc.handler;


import com.cc.command.Command;
import com.cc.core.RedisCoreImpl;
import com.cc.enmu.CMDTypeEnum;
import com.cc.protocal.resp.*;
import com.cc.server.redis.ChannelHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: cc-simple-redis
 * @description: Redis 连接管理/指令解析处理
 * @author: ccstar
 * @create: 2025-06-01  08:22
 **/
@Slf4j
public class RespCommandHandler extends SimpleChannelInboundHandler<Resp> {

    /**
     * 处理指令
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Resp msg) throws Exception {
        // todo 逻辑待优化
        if (msg instanceof RArrays) {
            RArrays array = (RArrays) msg;
            Resp[] content = array.getContent();
            String commandName = new String(((RBulkStrings)content[0]).getContent());
            commandName = commandName.toUpperCase();
                CMDTypeEnum cmdTypeEnum = null;
                try{
                    cmdTypeEnum = CMDTypeEnum.valueOf(commandName);
                }catch (IllegalArgumentException e){
                    ctx.channel().writeAndFlush(new RErrors("命令不存在!"));
                }
                if (cmdTypeEnum != null) {
                    Command apply = cmdTypeEnum.getSupplier().apply(new RedisCoreImpl());
                    ctx.channel().writeAndFlush(apply.handle());
                }
            }


    }

    /**
     * 连接激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelHolder.setChannel(ctx.channel().remoteAddress().toString(),ctx.channel());
    }

    /**
     * 连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Redis client connection close! IP:" + ctx.channel().remoteAddress().toString());
        ChannelHolder.removeChannel(ctx.channel().remoteAddress().toString());
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception connection close! IP:{}",ctx.channel().remoteAddress().toString());
        log.error("Redis connextion exception! Exception:{}",cause);
        ChannelHolder.removeChannel(ctx.channel().remoteAddress().toString());
    }
}
