package com.cc.raft.raft;

import com.example.raft.Raft.*;
import com.example.raft.RaftServiceGrpc.RaftServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RaftNode {
    private static final Logger logger = LoggerFactory.getLogger(RaftNode.class);

    // 节点状态
    private enum State { FOLLOWER, CANDIDATE, LEADER }
    private State state = State.FOLLOWER;

    // 持久化状态
    private int currentTerm = 0;
    private String votedFor = null;
    private List<LogEntry> log = new ArrayList<>();

    // 易失状态
    private int commitIndex = 0;
    private int lastApplied = 0;

    // 领导者专用状态
    private Map<String, Integer> nextIndex;
    private Map<String, Integer> matchIndex;

    // 节点信息
    private final String selfId;
    private final List<String> peerIds;
    private final Map<String, RaftServiceBlockingStub> peers = new ConcurrentHashMap<>();

    // 定时器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> electionTimer;
    private ScheduledFuture<?> heartbeatTimer;

    // 配置参数
    private final int electionTimeoutMin = 150; // 选举超时下限（毫秒）
    private final int electionTimeoutMax = 250; // 选举超时上限（毫秒）
    private final int heartbeatInterval = 50;   // 心跳间隔（毫秒）

    public RaftNode(String selfId, List<String> peerIds, Map<String, String> peerAddresses) {
        this.selfId = selfId;
        this.peerIds = peerIds;

        // 初始化日志，确保索引0有占位条目
        LogEntry placeholder = LogEntry.newBuilder().setTerm(0).build();
        log.add(placeholder);

        // 初始化与其他节点的连接
        for (String peerId : peerIds) {
            if (!peerId.equals(selfId)) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(peerAddresses.get(peerId))
                        .usePlaintext()
                        .keepAliveTime(30, TimeUnit.SECONDS)  // 保持长连接
                        .keepAliveWithoutCalls(true)
                        .build();
                peers.put(peerId, RaftServiceGrpc.newBlockingStub(channel));
            }
        }

        // 启动选举定时器
        resetElectionTimer();
    }

    // 重置选举定时器
    private void resetElectionTimer() {
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }

        long timeout = electionTimeoutMin + ThreadLocalRandom.current().nextLong(electionTimeoutMax - electionTimeoutMin);
        electionTimer = scheduler.schedule(this::startElection, timeout, TimeUnit.MILLISECONDS);

        logger.debug("节点 {} 重置选举定时器，超时: {}ms", selfId, timeout);
    }

    // 开始选举
    private synchronized void startElection() {
        if (state == State.LEADER) return;

        logger.info("节点 {} 开始选举，当前任期: {}", selfId, currentTerm);

        // 转为候选人状态
        state = State.CANDIDATE;
        currentTerm++;
        votedFor = selfId;
        AtomicInteger voteCount = new AtomicInteger(1); // 自己投自己一票

        // 向所有其他节点发送RequestVote请求
        for (String peerId : peerIds) {
            if (peerId.equals(selfId)) continue;

            CompletableFuture.runAsync(() -> {
                try {
                    RequestVoteRequest request = RequestVoteRequest.newBuilder()
                            .setTerm(currentTerm)
                            .setCandidateId(selfId)
                            .setLastLogIndex(getLastLogIndex())
                            .setLastLogTerm(getLastLogTerm())
                            .build();

                    RequestVoteResponse response = peers.get(peerId).requestVote(request);

                    synchronized (RaftNode.this) {
                        if (response.getTerm() > currentTerm) {
                            // 发现更高任期，转为跟随者
                            logger.info("节点 {} 发现更高任期 {}，转为跟随者", selfId, response.getTerm());
                            currentTerm = response.getTerm();
                            state = State.FOLLOWER;
                            votedFor = null;
                            resetElectionTimer();
                            return;
                        }

                        if (state == State.CANDIDATE && response.getVoteGranted()) {
                            voteCount.getAndIncrement();

                            // 获得多数选票，成为领导者
                            if (voteCount.get() > peerIds.size() / 2) {
                                becomeLeader();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("向节点 {} 发送RequestVote失败: {}", peerId, e.getMessage());
                }
            }, scheduler);
        }

        // 重置选举定时器
        resetElectionTimer();
    }

    // 成为领导者
    private synchronized void becomeLeader() {
        if (state != State.CANDIDATE) return;

        logger.info("节点 {} 在任期 {} 成为领导者", selfId, currentTerm);

        state = State.LEADER;

        // 初始化领导者状态
        nextIndex = new HashMap<>();
        matchIndex = new HashMap<>();
        for (String peerId : peerIds) {
            if (!peerId.equals(selfId)) {
                nextIndex.put(peerId, getLastLogIndex() + 1);
                matchIndex.put(peerId, 0);
            }
        }

        // 取消选举定时器
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }

        // 启动心跳定时器
        heartbeatTimer = scheduler.scheduleAtFixedRate(this::sendHeartbeats, 0, heartbeatInterval, TimeUnit.MILLISECONDS);

        logger.info("领导者 {} 开始发送心跳，nextIndex初始化: {}", selfId, nextIndex);
    }

    // 发送心跳（空的AppendEntries请求）
    private void sendHeartbeats() {
        if (state != State.LEADER) return;

        for (String peerId : peerIds) {
            if (peerId.equals(selfId)) continue;

            CompletableFuture.runAsync(() -> {
                try {
                    sendAppendEntries(peerId);
                } catch (Exception e) {
                    logger.error("向节点 {} 发送心跳失败: {}", peerId, e.getMessage());
                }
            }, scheduler);
        }
    }

    // 发送AppendEntries请求（修复后）
    private void sendAppendEntries(String peerId) {
        if (state != State.LEADER) return;

        synchronized (this) {
            int prevLogIndex = nextIndex.get(peerId) - 1;
            int prevLogTerm = (prevLogIndex >= 0 && prevLogIndex < log.size())
                    ? log.get(prevLogIndex).getTerm()
                    : 0;  // 索引非法时term为0

            // 构建请求
            AppendEntriesRequest.Builder requestBuilder = AppendEntriesRequest.newBuilder()
                    .setTerm(currentTerm)
                    .setLeaderId(selfId)
                    .setPrevLogIndex(prevLogIndex)
                    .setPrevLogTerm(prevLogTerm)
                    .setLeaderCommit(commitIndex);

            // 发送日志条目（带索引校验）
            if (nextIndex.get(peerId) <= getLastLogIndex()) {
                int index = nextIndex.get(peerId);
                if (index >= 0 && index < log.size()) {  // 核心校验
                    requestBuilder.setEntry(log.get(index));
                    logger.debug("节点 {} 向 {} 发送日志条目，索引: {}", selfId, peerId, index);
                } else {
                    logger.warn("节点 {} 日志索引 {} 越界（日志长度 {}），跳过发送",
                            selfId, index, log.size());
                }
            }

            AppendEntriesRequest request = requestBuilder.build();

            // 发送gRPC请求并处理响应
            try {
                AppendEntriesResponse response = peers.get(peerId).appendEntries(request);

                synchronized (RaftNode.this) {
                    if (response.getTerm() > currentTerm) {
                        // 发现更高任期，转为跟随者
                        logger.info("节点 {} 发现更高任期 {}，转为跟随者", selfId, response.getTerm());
                        currentTerm = response.getTerm();
                        state = State.FOLLOWER;
                        votedFor = null;

                        // 取消心跳定时器
                        if (heartbeatTimer != null) {
                            heartbeatTimer.cancel(false);
                        }

                        // 重置选举定时器
                        resetElectionTimer();
                        return;
                    }

                    if (response.getSuccess()) {
                        // 更新nextIndex和matchIndex
                        int oldNextIndex = nextIndex.get(peerId);
                        nextIndex.put(peerId, oldNextIndex + 1);
                        matchIndex.put(peerId, oldNextIndex);

                        logger.debug("节点 {} 向 {} 发送AppendEntries成功，nextIndex更新为 {}",
                                selfId, peerId, nextIndex.get(peerId));

                        // 更新提交索引
                        updateCommitIndex();
                    } else {
                        // 减少nextIndex并重试（限制最小值为0）
                        int oldNextIndex = nextIndex.get(peerId);
                        int newNextIndex = Math.max(oldNextIndex - 1, 0);
                        nextIndex.put(peerId, newNextIndex);

                        logger.info("节点 {} 向 {} 发送AppendEntries失败，nextIndex从 {} 调整为 {}",
                                selfId, peerId, oldNextIndex, newNextIndex);
                    }
                }
            } catch (Exception e) {
                logger.error("向节点 {} 发送AppendEntries异常: {}", peerId, e.getMessage());
            }
        }
    }

    // 更新提交索引
    private void updateCommitIndex() {
        if (state != State.LEADER) return;

        // 找到可以提交的最大索引
        for (int N = getLastLogIndex(); N > commitIndex; N--) {
            if (log.get(N).getTerm() != currentTerm) continue; // 只提交当前任期的日志

            int count = 1; // 领导者自己
            for (String peerId : peerIds) {
                if (!peerId.equals(selfId) && matchIndex.get(peerId) >= N) {
                    count++;
                }
            }

            if (count > peerIds.size() / 2) {
                commitIndex = N;
                logger.info("领导者 {} 提交索引更新为 {}", selfId, commitIndex);
                break;
            }
        }
    }

    // 处理RequestVote请求
    public synchronized RequestVoteResponse handleRequestVote(RequestVoteRequest request) {
        logger.debug("节点 {} 收到来自 {} 的RequestVote请求，任期 {}", selfId, request.getCandidateId(), request.getTerm());

        // 如果请求任期小于当前任期，拒绝投票
        if (request.getTerm() < currentTerm) {
            return RequestVoteResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setVoteGranted(false)
                    .build();
        }

        // 如果请求任期大于当前任期，转为跟随者
        if (request.getTerm() > currentTerm) {
            logger.info("节点 {} 因收到更高任期 {} 的RequestVote请求，转为跟随者", selfId, request.getTerm());
            currentTerm = request.getTerm();
            state = State.FOLLOWER;
            votedFor = null;
        }

        // 检查日志是否足够新
        boolean logUpToDate = (request.getLastLogTerm() > getLastLogTerm()) ||
                (request.getLastLogTerm() == getLastLogTerm() &&
                        request.getLastLogIndex() >= getLastLogIndex());

        // 决定是否投票
        boolean canVote = (votedFor == null || votedFor.equals(request.getCandidateId()));

        if (canVote && logUpToDate) {
            votedFor = request.getCandidateId();
            resetElectionTimer(); // 重置选举定时器

            logger.info("节点 {} 在任期 {} 投票给 {}", selfId, currentTerm, request.getCandidateId());

            return RequestVoteResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setVoteGranted(true)
                    .build();
        }

        return RequestVoteResponse.newBuilder()
                .setTerm(currentTerm)
                .setVoteGranted(false)
                .build();
    }

    // 处理AppendEntries请求
    public synchronized AppendEntriesResponse handleAppendEntries(AppendEntriesRequest request) {
        logger.info("节点 {} 收到来自 {} 的AppendEntries请求，任期 {}", selfId, request.getLeaderId(), request.getTerm());

        // 如果请求任期小于当前任期，拒绝
        if (request.getTerm() < currentTerm) {
            logger.debug("节点 {} 拒绝AppendEntries请求，任期 {} < {}", selfId, request.getTerm(), currentTerm);
            return AppendEntriesResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setSuccess(false)
                    .build();
        }

        // 如果请求任期大于当前任期，转为跟随者
        if (request.getTerm() > currentTerm) {
            logger.info("节点 {} 因收到更高任期 {} 的AppendEntries请求，转为跟随者", selfId, request.getTerm());
            currentTerm = request.getTerm();
            state = State.FOLLOWER;
            votedFor = null;
        }

        // 重置选举定时器
        resetElectionTimer();

        // 检查prevLogIndex是否存在
        if (request.getPrevLogIndex() > getLastLogIndex()) {
            logger.debug("节点 {} 拒绝AppendEntries请求，prevLogIndex {} 超出本地日志范围 {}",
                    selfId, request.getPrevLogIndex(), getLastLogIndex());
            return AppendEntriesResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setSuccess(false)
                    .build();
        }

        // 检查prevLogTerm是否匹配
        if (request.getPrevLogIndex() > 0 &&
                log.get(request.getPrevLogIndex()).getTerm() != request.getPrevLogTerm()) {
            logger.debug("节点 {} 拒绝AppendEntries请求，prevLogTerm不匹配（本地: {}, 请求: {}）",
                    selfId, log.get(request.getPrevLogIndex()).getTerm(), request.getPrevLogTerm());
            return AppendEntriesResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setSuccess(false)
                    .build();
        }

        // 处理日志条目（如果有）
        if (!request.getEntry().equals(LogEntry.getDefaultInstance())) {
            // 如果日志在该位置已存在且任期不同，则删除冲突日志
            if (request.getPrevLogIndex() + 1 <= getLastLogIndex()) {
                // 校验索引合法性
                if (request.getPrevLogIndex() + 1 < log.size()) {
                    if (log.get(request.getPrevLogIndex() + 1).getTerm() != request.getEntry().getTerm()) {
                        logger.info("节点 {} 删除索引 {} 及之后的日志", selfId, request.getPrevLogIndex() + 1);
                        log.subList(request.getPrevLogIndex() + 1, log.size()).clear();

                        // 确保日志不为空
                        if (log.isEmpty()) {
                            log.add(LogEntry.newBuilder().setTerm(0).build());
                        }
                    }
                }
            }

            // 添加新的日志条目
            if (request.getPrevLogIndex() + 1 > getLastLogIndex()) {
                log.add(request.getEntry());
                logger.info("节点 {} 添加日志条目: 索引 {}, 任期 {}", selfId, getLastLogIndex(), request.getEntry().getTerm());
            }
        }

        // 更新提交索引
        if (request.getLeaderCommit() > commitIndex) {
            commitIndex = Math.min(request.getLeaderCommit(), getLastLogIndex());
            logger.info("节点 {} 更新提交索引为 {}", selfId, commitIndex);
        }

        return AppendEntriesResponse.newBuilder()
                .setTerm(currentTerm)
                .setSuccess(true)
                .build();
    }

    // 辅助方法：获取最后日志条目的索引
    private int getLastLogIndex() {
        return log.size() - 1;
    }

    // 辅助方法：获取最后日志条目的任期
    private int getLastLogTerm() {
        if (getLastLogIndex() == 0) return 0;
        return log.get(getLastLogIndex()).getTerm();
    }

    // 关闭节点
    public void shutdown() {
        logger.info("节点 {} 正在关闭...", selfId);

        if (electionTimer != null) {
            electionTimer.cancel(false);
        }

        if (heartbeatTimer != null) {
            heartbeatTimer.cancel(false);
        }

        scheduler.shutdown();

        // 关闭gRPC通道
        for (RaftServiceBlockingStub stub : peers.values()) {
            ManagedChannel channel = (ManagedChannel) stub.getChannel();
            if (!channel.isShutdown()) {
                channel.shutdown();
            }
        }

        logger.info("节点 {} 已关闭", selfId);
    }
}