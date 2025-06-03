package com.cc.server.redis;

import com.cc.database.core.RedisCore;
import com.cc.handler.RespCMDHandler;
import com.cc.handler.RespDecoder;
import com.cc.handler.RespEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import lombok.AllArgsConstructor;


/**
 * @program: cc-simple-redis
 * @description:
 * @author: ccstar
 * @create: 2025-06-01  08:09
 **/

@AllArgsConstructor
public class RedisChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final RedisCore redisCore;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RespDecoder());
        pipeline.addLast(new RespCMDHandler(redisCore));
        pipeline.addLast(new RespEncoder());
//        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));


    }
}
