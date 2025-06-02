package com.cc.server.redis;

import com.cc.handler.RespCMDHandler;
import com.cc.handler.RespDecoder;
import com.cc.handler.RespEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;


/**
 * @program: cc-simple-redis
 * @description:
 * @author: ccstar
 * @create: 2025-06-01  08:09
 **/


public class RedisChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RespDecoder());
        pipeline.addLast(new RespCMDHandler());
        pipeline.addLast(new RespEncoder());
//        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));


    }
}
