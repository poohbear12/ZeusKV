package com.cc.enmu;

import com.cc.command.Command;
import com.cc.command.Ping;
import com.cc.core.RedisCore;
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

    PING(core -> new Ping());

    private final Function<RedisCore, Command> supplier;

    CMDTypeEnum(Function<RedisCore, Command> supplier) {
        this.supplier = supplier;
    }

}
