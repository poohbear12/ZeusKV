package com.cc.cmd.list;

import com.cc.cmd.AbstractCommand;
import com.cc.common.enmu.CMDTypeEnum;
import com.cc.database.core.RedisCore;
import com.cc.database.datastructure.RedisList;
import com.cc.database.datastructure.RedisObject;
import com.cc.database.datastructure.RedisString;
import com.cc.protocal.resp.RErrors;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: cc-simple-redis
 * @description:
 * @author: ccstar
 * @create: 2025-06-03  15:19
 **/
public class Lpush extends AbstractCommand {

    public Lpush(RedisCore redisCore) {
        super(redisCore);
    }

    @Override
    public CMDTypeEnum getCommand() {
        return null;
    }

    @Override
    public Resp handle() {
        if (content.length < 2) {
            return new RErrors("指令过短!");
        }
        int count = handlerArgs();
        return new RSimpleStrings(String.valueOf(count));
    }

    private int handlerArgs() {
        RedisString key = RedisString.Instance().setValue(content[0]);
        List<RedisString> list = new ArrayList<>();
        for (int i = 1; i < content.length; i++) {
            list.addFirst(RedisString.Instance().setValue(content[i]));
        }
        RedisList<RedisString> values = RedisList.Instance();
        values.lPush(list);
        redisCore.put(key,values);
        return content.length - 1;
    }
}
