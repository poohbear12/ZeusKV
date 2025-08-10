package com.cc.raft.raft;

import io.grpc.stub.StreamObserver;

public class RaftServiceImpl extends RaftServiceGrpc.RaftServiceImplBase {
    private final RaftNode raftNode;

    public RaftServiceImpl(RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    @Override
    public void requestVote(Raft.RequestVoteRequest request,
                            StreamObserver<Raft.RequestVoteResponse> responseObserver) {
        Raft.RequestVoteResponse response = raftNode.handleRequestVote(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void appendEntries(Raft.AppendEntriesRequest request,
                              StreamObserver<Raft.AppendEntriesResponse> responseObserver) {
        Raft.AppendEntriesResponse response = raftNode.handleAppendEntries(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
