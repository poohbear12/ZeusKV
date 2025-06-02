package com.cc.cmd;

import com.cc.common.enmu.CMDTypeEnum;
import com.cc.protocal.resp.RSimpleStrings;
import com.cc.protocal.resp.Resp;

/**
 * @program: cc-simple-redis
 * @description: 简易指令Ping 测试
 * @author: ccstar
 * @create: 2025-06-01  15:57
 **/


public class Ping implements Command{

    @Override
    public CMDTypeEnum getCommand() {
        return CMDTypeEnum.PING;
    }

    @Override
    public void setContext(Resp[] array) {

    }

    @Override
    public Resp handle() {
        return new RSimpleStrings("PONG");
    }
}
