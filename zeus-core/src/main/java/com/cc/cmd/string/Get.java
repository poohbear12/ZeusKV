package com.cc.cmd.string;

import com.cc.cmd.AbstractCommand;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.database.core.RedisCore;
import com.cc.database.datastructure.RedisString;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;

/**
 * @program: zeus-kv
 * @description: String get key -> value
 * @author: ccstar
 * @create: 2025-06-02  20:16
 **/

public class Get extends AbstractCommand {

  public Get(RedisCore redisCore) {
    super(redisCore);
  }


  @Override
  public CMDTypeEnum getCommand() {
    return CMDTypeEnum.GET;
  }


  // todo 逻辑未优化
  @Override
  public Resp handle() {
    try {
      RedisString value = handlerArgs();
      return new RSimpleStrings(value.getValue());
    } catch (Exception e) {
      return new RErrors("key不存在!");
    }

  }

  private RedisString handlerArgs() {
    RedisString key = RedisString.Instance().setValue(content[0]);
    return (RedisString) redisCore.get(key);
  }

}
