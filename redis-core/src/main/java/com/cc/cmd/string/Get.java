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
 * @description: String get key -> value
 * @author: ccstar
 * @create: 2025-06-02  20:16
 **/

@Data
public class Get implements Command {

    private RedisCore redisCore;

    private Resp[] array;

    @Override
    public CMDTypeEnum getCommand() {
        return CMDTypeEnum.GET;
    }

    @Override
    public void setContext(Resp[] array) {
        this.array = array;
    }
    // todo 逻辑未优化
    @Override
    public Resp handle() {
        RedisString key = new RedisString(((RBulkStrings)array[1]).getString());
        RedisString redisObject = (RedisString) redisCore.get(key);
        if (redisObject == null) {
            return new RErrors("指令异常!");
        }
        return new RSimpleStrings(redisObject.getValue());
    }

    public Get(RedisCore redisCore) {
        this.redisCore = redisCore;
    }
}
