package com.cc.cluster;

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
     * 2.
     */
    @Override
    public void initiateVoting(){
        // 1. 遍历集群节点，发送信息
        // 2. 设置发送机addr
        countDownLatch = new CountDownLatch(cluster.size());
        for (String nodeAddr : cluster) {
            Vote vote = new Vote();
            vote.setAddr(host);
            vote.setMsg("vote");
            emitMsg(nodeAddr, vote);
        }
    }

    /**
     * 投票
     */
    @Override
    public void castVote(){}

    /**
     * 发送消息 ： all
     * @param msg
     */
    @Override
    public void emitMsg(Msg msg) {
        for (String nodeAddr : cluster) {
            emitMsg(nodeAddr, msg);
        }
    }

    /**
     * 发送消息
     * @param addr
     * @param msg
     */
    @Override
    public void emitMsg(String addr, Msg msg) {
        Node node = TestEnv.map.get(addr);
        node.handVote((Vote) msg);
    }


    /**
     * 处理日志
     */
    @Override
    public void handleLog(Log log) {

    }

    /**
     * 处理心跳
     */
    @Override
    public void handHeart(Heart heart) {

    }

    /**
     * 处理投票信息
     */
    @Override
    public void handVote(Vote vote) {
        // 1. 收到投票信息
        String voteAddr = vote.getAddr();
        emitMsg(voteAddr, new Vote());
    }

}
