package com.cc.cmd.string;

import com.cc.cmd.Command;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.database.core.RedisCore;
import com.cc.database.datastructure.RedisString;
import com.cc.protocal.resp.RBulkStrings;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;
import lombok.Data;

/**
 * @program: cc-simple-redis
 * @description:set key name命令
 * @author: ccstar
 * @create: 2025-06-02  17:02
 **/

@Data
public class Set implements Command {

    private RedisCore redisCore;

    private Resp[] array;

    @Override
    public CMDTypeEnum getCommand() {
        return null;
    }

    @Override
    public void setContext(Resp[] array) {
        this.array = array;
    }

    // todo 逻辑未优化
    @Override
    public Resp handle() {
        if (array.length < 3) {
            return new RErrors("指令过少!");
        }
        RedisString key = new RedisString(((RBulkStrings) array[1]).getString());
        RedisString value = new RedisString(((RBulkStrings) array[2]).getString());
        redisCore.put(key,value);
        return new RSimpleStrings("OK");
    }

    public Set(RedisCore redisCore) {
        this.redisCore = redisCore;
    }
}
