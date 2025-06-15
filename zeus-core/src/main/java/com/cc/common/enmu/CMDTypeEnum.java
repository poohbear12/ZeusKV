package com.cc.common.enmu;

import com.cc.cmd.Command;
import com.cc.cmd.Ping;
import com.cc.cmd.list.Lpop;
import com.cc.cmd.list.Lpush;
import com.cc.cmd.string.Get;
import com.cc.cmd.string.Set;
import com.cc.database.core.RedisCore;
import java.util.function.Function;
import lombok.Getter;

/**
 * @program: zeus-kv
 * @description: 枚举命令类型
 * @author: ccstar
 * @create: 2025-06-01  11:22
 **/
@Getter
public enum CMDTypeEnum {

  PING(Ping::new),

  SET(Set::new),

  GET(Get::new),

  LPUSH(Lpush::new),

  LPOP(Lpop::new);

  private final Function<RedisCore, Command> supplier;

  CMDTypeEnum(Function<RedisCore, Command> supplier) {
    this.supplier = supplier;
  }

}
