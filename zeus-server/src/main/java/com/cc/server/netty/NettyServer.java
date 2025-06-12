package com.cc.server.netty;

import com.cc.config.entity.NodeConfig;
import com.cc.config.entity.ZeusConfig;
import com.cc.exception.ConfigException;
import com.cc.server.KVServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: zeus-kv
 * @description: Netty server 服务端
 * @author: ccstar
 * @create: 2025-06-01  07:57
 **/

@Slf4j
public class NettyServer implements KVServer {

    /**
     * 任务派发线程
     */
    private EventLoopGroup Acceptor;

    /**
     * 任务处理线程
     */
    private EventLoopGroup Handler;

    /**
     * 连接管理
     */
    private Channel serverChannel;

    /**
     * 最大连接数
     */
    private int maxConnections;

    /**
     * IP地址
     */
    private String addr;

    /**
     * 端口号
     */
    private int port;


    public NettyServer(ZeusConfig zeusConfig) {
        if (zeusConfig == null) {
            throw new ConfigException();
        }
        // 目前单机模式
        NodeConfig nodeConfig = zeusConfig.getNodes().get(0);
        this.addr = nodeConfig.getAddr();
        this.port = nodeConfig.getPort();
        this.Acceptor = new NioEventLoopGroup(1);
        this.Handler = new NioEventLoopGroup(1);
        this.maxConnections = nodeConfig.getMaxConnections();
        start();
    }

    /**
     * 服务器启动
     */
    @Override
    public void start() {
        log.info("Netty服务启动中!");
        try {
            serverChannel = new ServerBootstrap().
                    group(Acceptor, Handler).
                    channel(NioServerSocketChannel.class).
                    option(ChannelOption.SO_BACKLOG, maxConnections).
                    childHandler(new NettyChannelInitializer()).
                    bind(addr, port)
                    .sync().channel();

            log.info("启动IP地址:{},启动端口号:{}",addr, port);
            log.info("Netty服务启动成功!");
            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("线程被中断:{}",e);
        } finally {
            if (Acceptor != null) {
                Acceptor.shutdownGracefully();
            }
            if (Handler != null) {
                Handler.shutdownGracefully();
            }
        }
    }

    /**
     * 服务器关闭
     */
    @Override
    public void stop() {
        if (Acceptor != null) {
            Acceptor.shutdownGracefully();
        }
        if (Handler != null) {
            Handler.shutdownGracefully();
        }
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}
