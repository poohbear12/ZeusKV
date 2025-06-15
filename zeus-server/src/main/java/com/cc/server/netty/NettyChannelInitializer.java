package com.cc.server.netty;

import com.cc.server.netty.handler.RespCMDHandler;
import com.cc.server.netty.handler.RespDecoder;
import com.cc.server.netty.handler.RespEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;


/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-01  08:09
 **/

@AllArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {


  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast(new RespDecoder());
    pipeline.addLast(new RespCMDHandler());
    pipeline.addLast(new RespEncoder());
//        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));

  }
}
