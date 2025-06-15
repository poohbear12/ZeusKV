package com.cc.server.netty.handler;

import com.cc.cmd.Command;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.engine.EngineContainer;
import com.cc.protocal.resp.RArrays;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.Resp;
import com.cc.server.netty.NettyConnectionHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: zeus-kv
 * @description: Redis 连接管理/指令解析处理
 * @author: ccstar
 * @create: 2025-06-01  08:22
 **/
@Slf4j
@AllArgsConstructor
public class RespCMDHandler extends SimpleChannelInboundHandler<Resp> {

  /**
   * 处理指令
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Resp msg) throws Exception {
    // todo 逻辑待优化
    // 1. 通过解析命令名称来获取枚举类 -> 通过枚举类方法获取到命令实体 -> 执行命令方法 -> 获取到返回值
    if (msg instanceof RArrays) {
      Resp[] resps = ((RArrays) msg).getContent(); // 1. set 2. key 3. value...
      String commandName = new String(((RBulkStrings) resps[0]).getContent()).toUpperCase();
      try {
        CMDTypeEnum cmdTypeEnum = CMDTypeEnum.valueOf(commandName);
        Command cmd = cmdTypeEnum.getSupplier().apply(EngineContainer.getStoreCore()).setContext(resps);
        ctx.channel().writeAndFlush(cmd.handle());
        if (EngineContainer.getAOFManager() != null) {
          // todo 后续优化
          if (commandName.equals("SET") || commandName.equals("LPUSH") || commandName.equals("LPOP")) {
            EngineContainer.getAOFManager().append((RArrays) msg);
          }
        }
      } catch (IllegalArgumentException e) {
        ctx.channel().writeAndFlush(new RErrors("命令不存在!"));
      }
    }

  }

  /**
   * 连接激活
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    NettyConnectionHolder.setChannel(ctx.channel().remoteAddress().toString(), ctx.channel());
  }

  /**
   * 连接断开
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("Redis client connection close! IP:" + ctx.channel().remoteAddress().toString());
    NettyConnectionHolder.removeChannel(ctx.channel().remoteAddress().toString());
  }

  /**
   * 异常处理
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("exception connection close! IP:{}", ctx.channel().remoteAddress().toString());
    log.error("Redis connextion exception! Exception:{}", cause);
    NettyConnectionHolder.removeChannel(ctx.channel().remoteAddress().toString());
  }
}
