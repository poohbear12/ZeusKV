package com.cc.server.netty;

import com.cc.config.entity.NodeConfig;
import com.cc.config.entity.ZeusConfig;
import com.cc.exception.ConfigException;
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
 * @program: zeus-kv
 * @description: Netty server 服务端
 * @author: ccstar
 * @create: 2025-06-01  07:57
 **/


@Slf4j
public class NettyServer implements KVServer {

  /**
   * 启动成功标志
   */
  private static final boolean SUCCESS = false;
  /**
   * 任务派发线程
   */
  private final EventLoopGroup acceptor;
  /**
   * 任务处理线程
   */
  private final EventLoopGroup handler;
  /**
   * 最大连接数
   */
  private final int maxConnections;
  /**
   * IP地址
   */
  private final String addr;
  /**
   * 端口号
   */
  private final int port;
  /**
   * 连接管理
   */
  private Channel serverChannel;

  public NettyServer(ZeusConfig zeusConfig) {
    if (zeusConfig == null) {
      throw new ConfigException();
    }
    // 目前单机模式
    NodeConfig nodeConfig = zeusConfig.getNodes().get(0);
    this.addr = nodeConfig.getAddr();
    this.port = nodeConfig.getPort();
    this.acceptor = new NioEventLoopGroup(1);
    this.handler = new NioEventLoopGroup(1);
    this.maxConnections = nodeConfig.getMaxConnections();
    start();
  }

  /**
   * 服务器启动
   */
  @Override
  public void start() {
    ChannelFuture future = new ServerBootstrap().
        group(acceptor, handler).
        channel(NioServerSocketChannel.class).
        option(ChannelOption.SO_BACKLOG, maxConnections).
        childHandler(new NettyChannelInitializer()).
        bind(addr, port);
    future.addListener(f -> {
      if (f.isSuccess()) {
        serverChannel = future.channel();
        // 异步等待服务器关闭
        serverChannel.closeFuture().addListener(closeFuture -> {
        });
      } else {
        log.error("Netty服务启动失败{}", f.cause().getMessage());
      }
    });


  }

  /**
   * 服务器关闭
   */
  @Override
  public void stop() {
    if (acceptor != null) {
      acceptor.shutdownGracefully();
    }
    if (handler != null) {
      handler.shutdownGracefully();
    }
    if (serverChannel != null) {
      serverChannel.close();
    }
  }
}
