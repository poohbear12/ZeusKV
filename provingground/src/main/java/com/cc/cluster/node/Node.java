package com.cc.cluster.node;

import com.cc.cluster.message.Vote;
import com.cc.cluster.message.Heart;
import com.cc.cluster.message.Log;
import com.cc.cluster.message.Msg;

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

    /**
     * 根据线程执行其方法
     */
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
    }

    /**
     * 处理candicate节点
     */
    private void handlerCandicate() {

    }

    /**
     * 处理follower节点
     */
    private void handlerFollower() {

    }

    /**
     * 同步日志
     */
    private void syncLog() {
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
     * 发送消息 默认发送：all
     */
    public abstract void emitMsgs(Msg msg, Class clazz);

    /**
     * 指定IP地址发送
     * @param msg
     * @param clazz
     */
    public abstract void emitMsg(Msg msg, Class clazz);

    /**
     * 处理消息
     */
    public abstract void handlerMsg(Msg msg);

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
