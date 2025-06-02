package com.cc.cmd;


import com.cc.common.enmu.CMDTypeEnum;
import com.cc.protocal.resp.Resp;

public interface Command {
    /**
     * 获取命令类型
     * @return
     */
    CMDTypeEnum getCommand();

    /**
     * 设置命令执行内容
     * @param array
     */
    void setContext(Resp[] array);

    /**
     * 执行命令
     * @return
     */
    Resp handle();
}
