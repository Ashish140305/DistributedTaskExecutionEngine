package com.taskengine.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class WorkerServiceGrpc {

  private WorkerServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "taskengine.WorkerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.AssignTaskRequest,
      com.taskengine.proto.AssignTaskResponse> getAssignTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AssignTask",
      requestType = com.taskengine.proto.AssignTaskRequest.class,
      responseType = com.taskengine.proto.AssignTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.AssignTaskRequest,
      com.taskengine.proto.AssignTaskResponse> getAssignTaskMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.AssignTaskRequest, com.taskengine.proto.AssignTaskResponse> getAssignTaskMethod;
    if ((getAssignTaskMethod = WorkerServiceGrpc.getAssignTaskMethod) == null) {
      synchronized (WorkerServiceGrpc.class) {
        if ((getAssignTaskMethod = WorkerServiceGrpc.getAssignTaskMethod) == null) {
          WorkerServiceGrpc.getAssignTaskMethod = getAssignTaskMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.AssignTaskRequest, com.taskengine.proto.AssignTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AssignTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.AssignTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.AssignTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WorkerServiceMethodDescriptorSupplier("AssignTask"))
              .build();
        }
      }
    }
    return getAssignTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.CancelTaskRequest,
      com.taskengine.proto.CancelTaskResponse> getCancelTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelTask",
      requestType = com.taskengine.proto.CancelTaskRequest.class,
      responseType = com.taskengine.proto.CancelTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.CancelTaskRequest,
      com.taskengine.proto.CancelTaskResponse> getCancelTaskMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.CancelTaskRequest, com.taskengine.proto.CancelTaskResponse> getCancelTaskMethod;
    if ((getCancelTaskMethod = WorkerServiceGrpc.getCancelTaskMethod) == null) {
      synchronized (WorkerServiceGrpc.class) {
        if ((getCancelTaskMethod = WorkerServiceGrpc.getCancelTaskMethod) == null) {
          WorkerServiceGrpc.getCancelTaskMethod = getCancelTaskMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.CancelTaskRequest, com.taskengine.proto.CancelTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.CancelTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.CancelTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WorkerServiceMethodDescriptorSupplier("CancelTask"))
              .build();
        }
      }
    }
    return getCancelTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.GetWorkerStatusRequest,
      com.taskengine.proto.GetWorkerStatusResponse> getGetWorkerStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWorkerStatus",
      requestType = com.taskengine.proto.GetWorkerStatusRequest.class,
      responseType = com.taskengine.proto.GetWorkerStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.GetWorkerStatusRequest,
      com.taskengine.proto.GetWorkerStatusResponse> getGetWorkerStatusMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.GetWorkerStatusRequest, com.taskengine.proto.GetWorkerStatusResponse> getGetWorkerStatusMethod;
    if ((getGetWorkerStatusMethod = WorkerServiceGrpc.getGetWorkerStatusMethod) == null) {
      synchronized (WorkerServiceGrpc.class) {
        if ((getGetWorkerStatusMethod = WorkerServiceGrpc.getGetWorkerStatusMethod) == null) {
          WorkerServiceGrpc.getGetWorkerStatusMethod = getGetWorkerStatusMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.GetWorkerStatusRequest, com.taskengine.proto.GetWorkerStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetWorkerStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.GetWorkerStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.GetWorkerStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new WorkerServiceMethodDescriptorSupplier("GetWorkerStatus"))
              .build();
        }
      }
    }
    return getGetWorkerStatusMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WorkerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WorkerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WorkerServiceStub>() {
        @java.lang.Override
        public WorkerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WorkerServiceStub(channel, callOptions);
        }
      };
    return WorkerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static WorkerServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WorkerServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WorkerServiceBlockingV2Stub>() {
        @java.lang.Override
        public WorkerServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WorkerServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return WorkerServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WorkerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WorkerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WorkerServiceBlockingStub>() {
        @java.lang.Override
        public WorkerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WorkerServiceBlockingStub(channel, callOptions);
        }
      };
    return WorkerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WorkerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WorkerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WorkerServiceFutureStub>() {
        @java.lang.Override
        public WorkerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WorkerServiceFutureStub(channel, callOptions);
        }
      };
    return WorkerServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Task assignment — coordinator pushes a task to the worker
     * </pre>
     */
    default void assignTask(com.taskengine.proto.AssignTaskRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.AssignTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAssignTaskMethod(), responseObserver);
    }

    /**
     * <pre>
     * Task cancellation — coordinator tells worker to stop a task
     * </pre>
     */
    default void cancelTask(com.taskengine.proto.CancelTaskRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.CancelTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelTaskMethod(), responseObserver);
    }

    /**
     * <pre>
     * Status query — coordinator checks worker health and load
     * </pre>
     */
    default void getWorkerStatus(com.taskengine.proto.GetWorkerStatusRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.GetWorkerStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetWorkerStatusMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service WorkerService.
   */
  public static abstract class WorkerServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return WorkerServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service WorkerService.
   */
  public static final class WorkerServiceStub
      extends io.grpc.stub.AbstractAsyncStub<WorkerServiceStub> {
    private WorkerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WorkerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WorkerServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Task assignment — coordinator pushes a task to the worker
     * </pre>
     */
    public void assignTask(com.taskengine.proto.AssignTaskRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.AssignTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAssignTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Task cancellation — coordinator tells worker to stop a task
     * </pre>
     */
    public void cancelTask(com.taskengine.proto.CancelTaskRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.CancelTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Status query — coordinator checks worker health and load
     * </pre>
     */
    public void getWorkerStatus(com.taskengine.proto.GetWorkerStatusRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.GetWorkerStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetWorkerStatusMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service WorkerService.
   */
  public static final class WorkerServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<WorkerServiceBlockingV2Stub> {
    private WorkerServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WorkerServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WorkerServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Task assignment — coordinator pushes a task to the worker
     * </pre>
     */
    public com.taskengine.proto.AssignTaskResponse assignTask(com.taskengine.proto.AssignTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getAssignTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task cancellation — coordinator tells worker to stop a task
     * </pre>
     */
    public com.taskengine.proto.CancelTaskResponse cancelTask(com.taskengine.proto.CancelTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Status query — coordinator checks worker health and load
     * </pre>
     */
    public com.taskengine.proto.GetWorkerStatusResponse getWorkerStatus(com.taskengine.proto.GetWorkerStatusRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetWorkerStatusMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service WorkerService.
   */
  public static final class WorkerServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<WorkerServiceBlockingStub> {
    private WorkerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WorkerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WorkerServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Task assignment — coordinator pushes a task to the worker
     * </pre>
     */
    public com.taskengine.proto.AssignTaskResponse assignTask(com.taskengine.proto.AssignTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAssignTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task cancellation — coordinator tells worker to stop a task
     * </pre>
     */
    public com.taskengine.proto.CancelTaskResponse cancelTask(com.taskengine.proto.CancelTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Status query — coordinator checks worker health and load
     * </pre>
     */
    public com.taskengine.proto.GetWorkerStatusResponse getWorkerStatus(com.taskengine.proto.GetWorkerStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetWorkerStatusMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service WorkerService.
   */
  public static final class WorkerServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<WorkerServiceFutureStub> {
    private WorkerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WorkerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WorkerServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Task assignment — coordinator pushes a task to the worker
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.AssignTaskResponse> assignTask(
        com.taskengine.proto.AssignTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAssignTaskMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Task cancellation — coordinator tells worker to stop a task
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.CancelTaskResponse> cancelTask(
        com.taskengine.proto.CancelTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Status query — coordinator checks worker health and load
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.GetWorkerStatusResponse> getWorkerStatus(
        com.taskengine.proto.GetWorkerStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetWorkerStatusMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ASSIGN_TASK = 0;
  private static final int METHODID_CANCEL_TASK = 1;
  private static final int METHODID_GET_WORKER_STATUS = 2;

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

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ASSIGN_TASK:
          serviceImpl.assignTask((com.taskengine.proto.AssignTaskRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.AssignTaskResponse>) responseObserver);
          break;
        case METHODID_CANCEL_TASK:
          serviceImpl.cancelTask((com.taskengine.proto.CancelTaskRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.CancelTaskResponse>) responseObserver);
          break;
        case METHODID_GET_WORKER_STATUS:
          serviceImpl.getWorkerStatus((com.taskengine.proto.GetWorkerStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.GetWorkerStatusResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
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
          getAssignTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.AssignTaskRequest,
              com.taskengine.proto.AssignTaskResponse>(
                service, METHODID_ASSIGN_TASK)))
        .addMethod(
          getCancelTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.CancelTaskRequest,
              com.taskengine.proto.CancelTaskResponse>(
                service, METHODID_CANCEL_TASK)))
        .addMethod(
          getGetWorkerStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.GetWorkerStatusRequest,
              com.taskengine.proto.GetWorkerStatusResponse>(
                service, METHODID_GET_WORKER_STATUS)))
        .build();
  }

  private static abstract class WorkerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WorkerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.taskengine.proto.WorkerServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WorkerService");
    }
  }

  private static final class WorkerServiceFileDescriptorSupplier
      extends WorkerServiceBaseDescriptorSupplier {
    WorkerServiceFileDescriptorSupplier() {}
  }

  private static final class WorkerServiceMethodDescriptorSupplier
      extends WorkerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    WorkerServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WorkerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WorkerServiceFileDescriptorSupplier())
              .addMethod(getAssignTaskMethod())
              .addMethod(getCancelTaskMethod())
              .addMethod(getGetWorkerStatusMethod())
              .build();
        }
      }
    }
    return result;
  }
}
