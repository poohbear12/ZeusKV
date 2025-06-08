package com.cc.server.zeus;

import com.cc.database.core.RedisCore;
import com.cc.persistence.aof.AOFManager;
import com.cc.server.AbstractKVServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @program: zeus-kv
 * @description: Zeus server 服务端
 * @author: ccstar
 * @create: 2025-06-01  07:57
 **/

@Slf4j
public class ZeusKVServer extends AbstractKVServer {

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * Redis存储核心
     */
    private final RedisCore redisCore;

    /**
     * 任务派发线程
     */
    private EventLoopGroup bossGroup;

    /**
     * 任务处理线程
     */
    private EventLoopGroup workerGroup;

    /**
     * 连接管理
     */
    private Channel serverChannel;

    /**
     * AOF管理器
     */
    private AOFManager aofManager;

    /**
     * 是否启用AOF
     */
    private boolean EnableAOF = true;

    /**
     * AOF 刷盘间隔 1000 MS
     */
    private int flushInterval = 1000;


    public ZeusKVServer(String host, int port, RedisCore redisCore) throws IOException {
        this.host = host;
        this.port = port;
        this.redisCore = redisCore;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(1);
        if (EnableAOF) {
            this.aofManager = new AOFManager("redis.aof",flushInterval,redisCore);
        }
    }

    @Override
    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ZeusChannelInitializer(this.redisCore, this.aofManager));
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
