package com.cc.cmd.list;

import com.cc.cmd.AbstractCommand;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.database.core.RedisCore;
import com.cc.database.datastructure.RedisList;
import com.cc.database.datastructure.RedisString;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-03  15:19
 **/


public class Lpop extends AbstractCommand {


    @Override
    public CMDTypeEnum getCommand() {
        return CMDTypeEnum.LPOP;
    }

    @Override
    public Resp handle() {
        try {
            RedisList<RedisString> list = handlerArgs();
            RedisString redisObject = list.lPop();
            return new RSimpleStrings(redisObject.getValue());
        } catch (RuntimeException e) {
            return new RErrors("Lpop: 指令异常");
        }
    }

    private RedisList<RedisString> handlerArgs() {
        RedisString key = RedisString.Instance().setValue(content[0]);
        return (RedisList<RedisString>) redisCore.get(key);

    }
    public Lpop(RedisCore redisCore) {
        super(redisCore);
    }
}
