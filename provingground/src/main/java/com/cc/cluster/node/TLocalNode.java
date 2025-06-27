package com.cc.cluster.node;

import com.cc.cluster.TestEnv;
import com.cc.cluster.message.Vote;
import com.cc.cluster.message.Heart;
import com.cc.cluster.message.Log;
import com.cc.cluster.message.Msg;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @program: ZeusKV
 * @description: 集群节点类 本地测试节点类型
 * @author: ccstar
 * @create: 2025-06-25  12:08
 **/

@Data
public class TLocalNode extends Node{
    /**
     * 节点类型
     * 0 : Leader
     * 1 : Candidate
     * 2 : Follwer
     */
    private byte nodeType = 1;

    /**
     * Leader IP地址
     */
    private String addr;

    /**
     *  本机 IP地址
     */
    private String host;

    /**
     * 任期号
     */
    private int term;

    /**
     * 日志索引
     */
    private int logIdx;

    /**
     * 集群节点位置
     */
    private List<String> cluster;


    public TLocalNode(List<String> cluster, String host) {
        cluster.remove(host);
        this.cluster = cluster;
        this.host = host;
    }

    /**
     * 发起投票
     * 1. 向集群内其他节点发送投票信息
     */
    @Override
    public void initiateVoting(){
        // 1. 遍历集群节点，发送信息
        // 2. 设置原地址host
        // 3. 设置信息vote
    }

    @Override
    public void emitMsgs(Msg msg, Class clazz) {

    }

    @Override
    public void emitMsg(Msg msg, Class clazz) {

    }


    @Override
    public void handlerMsg(Msg msg) {

    }


}
