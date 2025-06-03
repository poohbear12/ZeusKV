package com.cc.server.redis;

import com.cc.database.core.RedisCore;
import com.cc.database.core.RedisCoreImpl;
import com.cc.server.AbstractKVServer;
import com.cc.server.KVServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: cc-simple-redis
 * @description: Redis server 服务端
 * @author: ccstar
 * @create: 2025-06-01  07:57
 **/

@Slf4j
public class RedisServer extends AbstractKVServer {

    private String host;
    private int port;
    private final RedisCore redisCore;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public RedisServer(String host, int port, RedisCore redisCore) {
        this.host = host;
        this.port = port;
        this.redisCore = redisCore;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(1);
    }

    @Override
    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new RedisChannelInitializer(this.redisCore));
            serverChannel = serverBootstrap.bind(host, port).sync().channel();
            log.info("Redis server started on port {}", port);
            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("Redis server start fail!");
            log.error("Exception:{}",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    @Override
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}
