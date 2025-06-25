package com.cc.cluster;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
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
    private String leaderAddr;

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
     * 线程
     */
    private Thread thread;

    /**
     * 集群节点位置
     */
    private List<String> addrs;

    /**
     * 节点启动标志
     */
    private static boolean flag = true;

    /**
     * CountDownLacth 接收投票
     */
    protected CountDownLatch countDownLatch;

    /**
     * 节点启动
     */
    public void start(){
        thread = new Thread(this::startIN);
        thread.start();
    }

    private void startIN() {
        flag = true;
        while (flag) {
            // 1. 判断当前节点类型
            // 2. 判断是否有leader节点
            // e. 如果当前节点不是Leader节点，同时leaderAddr为空 -> 触发选举
            switch (nodeType) {
                case 0:
                    handlerLeader();
                    break;
                case 1:
                    handlerCandicate();
                    break;
                case 2:
                    handlerFollower();
                    break;
                default:throw new RuntimeException("节点类型异常！");
            }
        }
    }

    /**
     * 处理leader节点
     */
    private void handlerLeader() {
        // 1. 发送心跳
        sendHeartBeat();
        // 2. 同步日志
        syncLog();
    }

    private void syncLog() {
    }

    private void sendHeartBeat() {
    }

    /**
     * 处理candicate节点
     */
    private void handlerCandicate() {
        initiateVoting();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {

        }
        // 统计投票结果
        // 变更状态
        // 同步信息
    }

    /**
     * 处理follower节点
     */
    private void handlerFollower() {

    }

    /**
     * 优雅关闭
     */
    public void graceStop() {
        this.flag = false;
    }

    public void stop() {

    }

    /**
     * 发起投票
     */
    public abstract void initiateVoting();

    /**
     * 投票
     */
    public abstract void castVote();

    /**
     * 发送消息 默认发送：all
     */
    public abstract void emitMsg(Msg msg);

    /**
     * 指定ip地址发送
     * @param addr
     */
    public abstract void emitMsg(String addr, Msg msg);

    /**
     * 处理日志
     */
    public abstract void handleLog(Log log);

    /**
     * 处理心跳
     */
    public abstract void handHeart(Heart heart);

    /**
     * 处理投票信息
     */
    public abstract void handVote(Vote vote);

    /**
     * 设置节点类型为Leader
     */
    protected void setLeader() {
        this.nodeType = 0;
    };

    /**
     * 设置节点类型为Candidate
     */
    protected void setCandidate() {
        this.nodeType = 1;
    };

    /**
     * 设置节点类型为Follwer
     */
    protected void setFollwer() {
        this.nodeType = 2;
    };
}
