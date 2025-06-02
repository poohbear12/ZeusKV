package com.cc.handler;

import com.cc.cmd.Command;


import com.cc.common.enmu.CMDTypeEnum;
import com.cc.database.core.RedisCore;
import com.cc.database.core.RedisCoreImpl;
import com.cc.protocal.resp.RArrays;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.Resp;
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
public class RespCMDHandler extends SimpleChannelInboundHandler<Resp> {
    private final RedisCore redisCore = new RedisCoreImpl();
    /**
     * 处理指令
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Resp msg) throws Exception {
        // todo 逻辑待优化
        // 1. 通过解析命令名称来获取枚举类 -> 通过枚举类方法获取到命令实体 -> 执行命令方法 -> 获取到返回值
        // 2. 逻辑部分还可以优化
        if (msg instanceof RArrays) {
            RArrays array = (RArrays) msg;
            Resp[] content = array.getContent();
            String commandName = new String(((RBulkStrings) content[0]).getContent()).toUpperCase();
            try {
                CMDTypeEnum cmdTypeEnum = CMDTypeEnum.valueOf(commandName);
                Command cmd = cmdTypeEnum.getSupplier().apply(redisCore);
                cmd.setContext(content);
                Resp handle = cmd.handle();
                ctx.channel().writeAndFlush(handle);
            } catch (IllegalArgumentException e) {
                ctx.channel().writeAndFlush(new RErrors("命令不存在!"));
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
