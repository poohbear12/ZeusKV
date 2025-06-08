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
 * @description:set key name命令
 * @author: ccstar
 * @create: 2025-06-02  17:02
 **/

public class Set extends AbstractCommand {


    @Override
    public CMDTypeEnum getCommand() {
        return CMDTypeEnum.SET;
    }

    // todo 逻辑未优化
    @Override
    public Resp handle() {
        if (content.length < 2) {
            return new RErrors("Set: 参数缺失!");
        }
        handlerArgs();
        return new RSimpleStrings("OK");
    }

    private void handlerArgs() {
        RedisString key = RedisString.Instance().setValue(content[0]);
        RedisString value = RedisString.Instance().setValue(content[1]);
        redisCore.put(key,value);
    }

    public Set(RedisCore redisCore) {
        super(redisCore);
    }

}
