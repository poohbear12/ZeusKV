package com.cc.cmd;

import com.cc.database.core.RedisCore;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.Resp;

/**
 * @program: zeus-kv
 * @description: 抽象命令类
 * @author: ccstar
 * @create: 2025-06-03  16:46
 **/

public abstract class AbstractCommand implements Command {

  protected final RedisCore redisCore;

  protected String[] content;

  public AbstractCommand(RedisCore redisCore) {
    this.redisCore = redisCore;
  }

  /**
   * Resp[] -> String[] 协议解析 key value value
   *
   * @param resps
   * @return
   */
  @Override
  public Command setContext(Resp[] resps) {
    this.content = new String[resps.length - 1];
    for (int i = 1; i < resps.length; i++) {
      content[i - 1] = new String(((RBulkStrings) resps[i]).getContent());
    }
    return this;
  }


}
