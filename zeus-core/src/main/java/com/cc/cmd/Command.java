package com.cc.cmd;


import com.cc.common.enmu.CMDTypeEnum;
import com.cc.protocal.resp.Resp;

public interface Command {
  /**
   * 获取命令类型
   *
   * @return
   */
  CMDTypeEnum getCommand();

  /**
   * 命令内容
   *
   * @param content
   */
  Command setContext(Resp[] content);

  /**
   * 执行命令
   *
   * @return
   */
  Resp handle();
}
