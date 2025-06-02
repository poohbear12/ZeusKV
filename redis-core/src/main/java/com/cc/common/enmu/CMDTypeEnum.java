package com.cc.common.enmu;

import com.cc.cmd.Command;
import com.cc.cmd.Ping;
import com.cc.cmd.string.Get;
import com.cc.cmd.string.Set;
import com.cc.database.core.RedisCore;
import lombok.Getter;

import java.util.function.Function;

/**
 * @program: cc-simple-redis
 * @description: 枚举命令类型
 * @author: ccstar
 * @create: 2025-06-01  11:22
 **/
@Getter
public enum CMDTypeEnum {

    PING(core -> new Ping()),

    SET(Set::new),

    GET(Get::new);

    private final Function<RedisCore, Command> supplier;

    CMDTypeEnum(Function<RedisCore, Command> supplier) {
        this.supplier = supplier;
    }

}
