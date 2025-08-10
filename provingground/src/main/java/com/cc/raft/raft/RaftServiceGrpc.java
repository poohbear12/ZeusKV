package com.cc.raft.raft;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 3. 服务定义
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.0)",
    comments = "Source: raft.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RaftServiceGrpc {

  private RaftServiceGrpc() {}

  public static final String SERVICE_NAME = "com.example.raft.RaftService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Raft.RequestVoteRequest,
      Raft.RequestVoteResponse> getRequestVoteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestVote",
      requestType = Raft.RequestVoteRequest.class,
      responseType = Raft.RequestVoteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Raft.RequestVoteRequest,
      Raft.RequestVoteResponse> getRequestVoteMethod() {
    io.grpc.MethodDescriptor<Raft.RequestVoteRequest, Raft.RequestVoteResponse> getRequestVoteMethod;
    if ((getRequestVoteMethod = RaftServiceGrpc.getRequestVoteMethod) == null) {
      synchronized (RaftServiceGrpc.class) {
        if ((getRequestVoteMethod = RaftServiceGrpc.getRequestVoteMethod) == null) {
          RaftServiceGrpc.getRequestVoteMethod = getRequestVoteMethod =
              io.grpc.MethodDescriptor.<Raft.RequestVoteRequest, Raft.RequestVoteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestVote"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Raft.RequestVoteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Raft.RequestVoteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServiceMethodDescriptorSupplier("RequestVote"))
              .build();
        }
      }
    }
    return getRequestVoteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Raft.AppendEntriesRequest,
      Raft.AppendEntriesResponse> getAppendEntriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AppendEntries",
      requestType = Raft.AppendEntriesRequest.class,
      responseType = Raft.AppendEntriesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Raft.AppendEntriesRequest,
      Raft.AppendEntriesResponse> getAppendEntriesMethod() {
    io.grpc.MethodDescriptor<Raft.AppendEntriesRequest, Raft.AppendEntriesResponse> getAppendEntriesMethod;
    if ((getAppendEntriesMethod = RaftServiceGrpc.getAppendEntriesMethod) == null) {
      synchronized (RaftServiceGrpc.class) {
        if ((getAppendEntriesMethod = RaftServiceGrpc.getAppendEntriesMethod) == null) {
          RaftServiceGrpc.getAppendEntriesMethod = getAppendEntriesMethod =
              io.grpc.MethodDescriptor.<Raft.AppendEntriesRequest, Raft.AppendEntriesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AppendEntries"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Raft.AppendEntriesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Raft.AppendEntriesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftServiceMethodDescriptorSupplier("AppendEntries"))
              .build();
        }
      }
    }
    return getAppendEntriesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RaftServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftServiceStub>() {
        @Override
        public RaftServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftServiceStub(channel, callOptions);
        }
      };
    return RaftServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RaftServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftServiceBlockingStub>() {
        @Override
        public RaftServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftServiceBlockingStub(channel, callOptions);
        }
      };
    return RaftServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RaftServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftServiceFutureStub>() {
        @Override
        public RaftServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftServiceFutureStub(channel, callOptions);
        }
      };
    return RaftServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 3. 服务定义
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void requestVote(Raft.RequestVoteRequest request,
                             io.grpc.stub.StreamObserver<Raft.RequestVoteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestVoteMethod(), responseObserver);
    }

    /**
     */
    default void appendEntries(Raft.AppendEntriesRequest request,
                               io.grpc.stub.StreamObserver<Raft.AppendEntriesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAppendEntriesMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RaftService.
   * <pre>
   * 3. 服务定义
   * </pre>
   */
  public static abstract class RaftServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return RaftServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RaftService.
   * <pre>
   * 3. 服务定义
   * </pre>
   */
  public static final class RaftServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RaftServiceStub> {
    private RaftServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RaftServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftServiceStub(channel, callOptions);
    }

    /**
     */
    public void requestVote(Raft.RequestVoteRequest request,
                            io.grpc.stub.StreamObserver<Raft.RequestVoteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void appendEntries(Raft.AppendEntriesRequest request,
                              io.grpc.stub.StreamObserver<Raft.AppendEntriesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RaftService.
   * <pre>
   * 3. 服务定义
   * </pre>
   */
  public static final class RaftServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RaftServiceBlockingStub> {
    private RaftServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RaftServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public Raft.RequestVoteResponse requestVote(Raft.RequestVoteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestVoteMethod(), getCallOptions(), request);
    }

    /**
     */
    public Raft.AppendEntriesResponse appendEntries(Raft.AppendEntriesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAppendEntriesMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RaftService.
   * <pre>
   * 3. 服务定义
   * </pre>
   */
  public static final class RaftServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RaftServiceFutureStub> {
    private RaftServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RaftServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Raft.RequestVoteResponse> requestVote(
        Raft.RequestVoteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Raft.AppendEntriesResponse> appendEntries(
        Raft.AppendEntriesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_VOTE = 0;
  private static final int METHODID_APPEND_ENTRIES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_VOTE:
          serviceImpl.requestVote((Raft.RequestVoteRequest) request,
              (io.grpc.stub.StreamObserver<Raft.RequestVoteResponse>) responseObserver);
          break;
        case METHODID_APPEND_ENTRIES:
          serviceImpl.appendEntries((Raft.AppendEntriesRequest) request,
              (io.grpc.stub.StreamObserver<Raft.AppendEntriesResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRequestVoteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Raft.RequestVoteRequest,
              Raft.RequestVoteResponse>(
                service, METHODID_REQUEST_VOTE)))
        .addMethod(
          getAppendEntriesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Raft.AppendEntriesRequest,
              Raft.AppendEntriesResponse>(
                service, METHODID_APPEND_ENTRIES)))
        .build();
  }

  private static abstract class RaftServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RaftServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Raft.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RaftService");
    }
  }

  private static final class RaftServiceFileDescriptorSupplier
      extends RaftServiceBaseDescriptorSupplier {
    RaftServiceFileDescriptorSupplier() {}
  }

  private static final class RaftServiceMethodDescriptorSupplier
      extends RaftServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RaftServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RaftServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RaftServiceFileDescriptorSupplier())
              .addMethod(getRequestVoteMethod())
              .addMethod(getAppendEntriesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
