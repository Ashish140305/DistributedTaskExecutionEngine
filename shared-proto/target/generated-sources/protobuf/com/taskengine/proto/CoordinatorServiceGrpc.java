package com.taskengine.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class CoordinatorServiceGrpc {

  private CoordinatorServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "taskengine.CoordinatorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.RegisterWorkerRequest,
      com.taskengine.proto.RegisterWorkerResponse> getRegisterWorkerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterWorker",
      requestType = com.taskengine.proto.RegisterWorkerRequest.class,
      responseType = com.taskengine.proto.RegisterWorkerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.RegisterWorkerRequest,
      com.taskengine.proto.RegisterWorkerResponse> getRegisterWorkerMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.RegisterWorkerRequest, com.taskengine.proto.RegisterWorkerResponse> getRegisterWorkerMethod;
    if ((getRegisterWorkerMethod = CoordinatorServiceGrpc.getRegisterWorkerMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getRegisterWorkerMethod = CoordinatorServiceGrpc.getRegisterWorkerMethod) == null) {
          CoordinatorServiceGrpc.getRegisterWorkerMethod = getRegisterWorkerMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.RegisterWorkerRequest, com.taskengine.proto.RegisterWorkerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterWorker"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.RegisterWorkerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.RegisterWorkerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("RegisterWorker"))
              .build();
        }
      }
    }
    return getRegisterWorkerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.HeartbeatRequest,
      com.taskengine.proto.HeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = com.taskengine.proto.HeartbeatRequest.class,
      responseType = com.taskengine.proto.HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.HeartbeatRequest,
      com.taskengine.proto.HeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.HeartbeatRequest, com.taskengine.proto.HeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = CoordinatorServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getHeartbeatMethod = CoordinatorServiceGrpc.getHeartbeatMethod) == null) {
          CoordinatorServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.HeartbeatRequest, com.taskengine.proto.HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.HeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.SubmitJobRequest,
      com.taskengine.proto.SubmitJobResponse> getSubmitJobMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitJob",
      requestType = com.taskengine.proto.SubmitJobRequest.class,
      responseType = com.taskengine.proto.SubmitJobResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.SubmitJobRequest,
      com.taskengine.proto.SubmitJobResponse> getSubmitJobMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.SubmitJobRequest, com.taskengine.proto.SubmitJobResponse> getSubmitJobMethod;
    if ((getSubmitJobMethod = CoordinatorServiceGrpc.getSubmitJobMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getSubmitJobMethod = CoordinatorServiceGrpc.getSubmitJobMethod) == null) {
          CoordinatorServiceGrpc.getSubmitJobMethod = getSubmitJobMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.SubmitJobRequest, com.taskengine.proto.SubmitJobResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitJob"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.SubmitJobRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.SubmitJobResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("SubmitJob"))
              .build();
        }
      }
    }
    return getSubmitJobMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.GetJobStatusRequest,
      com.taskengine.proto.GetJobStatusResponse> getGetJobStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetJobStatus",
      requestType = com.taskengine.proto.GetJobStatusRequest.class,
      responseType = com.taskengine.proto.GetJobStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.GetJobStatusRequest,
      com.taskengine.proto.GetJobStatusResponse> getGetJobStatusMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.GetJobStatusRequest, com.taskengine.proto.GetJobStatusResponse> getGetJobStatusMethod;
    if ((getGetJobStatusMethod = CoordinatorServiceGrpc.getGetJobStatusMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getGetJobStatusMethod = CoordinatorServiceGrpc.getGetJobStatusMethod) == null) {
          CoordinatorServiceGrpc.getGetJobStatusMethod = getGetJobStatusMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.GetJobStatusRequest, com.taskengine.proto.GetJobStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetJobStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.GetJobStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.GetJobStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("GetJobStatus"))
              .build();
        }
      }
    }
    return getGetJobStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskCompletionRequest,
      com.taskengine.proto.ReportTaskCompletionResponse> getReportTaskCompletionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReportTaskCompletion",
      requestType = com.taskengine.proto.ReportTaskCompletionRequest.class,
      responseType = com.taskengine.proto.ReportTaskCompletionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskCompletionRequest,
      com.taskengine.proto.ReportTaskCompletionResponse> getReportTaskCompletionMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskCompletionRequest, com.taskengine.proto.ReportTaskCompletionResponse> getReportTaskCompletionMethod;
    if ((getReportTaskCompletionMethod = CoordinatorServiceGrpc.getReportTaskCompletionMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getReportTaskCompletionMethod = CoordinatorServiceGrpc.getReportTaskCompletionMethod) == null) {
          CoordinatorServiceGrpc.getReportTaskCompletionMethod = getReportTaskCompletionMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.ReportTaskCompletionRequest, com.taskengine.proto.ReportTaskCompletionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReportTaskCompletion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.ReportTaskCompletionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.ReportTaskCompletionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("ReportTaskCompletion"))
              .build();
        }
      }
    }
    return getReportTaskCompletionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskFailureRequest,
      com.taskengine.proto.ReportTaskFailureResponse> getReportTaskFailureMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReportTaskFailure",
      requestType = com.taskengine.proto.ReportTaskFailureRequest.class,
      responseType = com.taskengine.proto.ReportTaskFailureResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskFailureRequest,
      com.taskengine.proto.ReportTaskFailureResponse> getReportTaskFailureMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.ReportTaskFailureRequest, com.taskengine.proto.ReportTaskFailureResponse> getReportTaskFailureMethod;
    if ((getReportTaskFailureMethod = CoordinatorServiceGrpc.getReportTaskFailureMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getReportTaskFailureMethod = CoordinatorServiceGrpc.getReportTaskFailureMethod) == null) {
          CoordinatorServiceGrpc.getReportTaskFailureMethod = getReportTaskFailureMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.ReportTaskFailureRequest, com.taskengine.proto.ReportTaskFailureResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReportTaskFailure"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.ReportTaskFailureRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.ReportTaskFailureResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("ReportTaskFailure"))
              .build();
        }
      }
    }
    return getReportTaskFailureMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.taskengine.proto.CancelJobRequest,
      com.taskengine.proto.CancelJobResponse> getCancelJobMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelJob",
      requestType = com.taskengine.proto.CancelJobRequest.class,
      responseType = com.taskengine.proto.CancelJobResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.taskengine.proto.CancelJobRequest,
      com.taskengine.proto.CancelJobResponse> getCancelJobMethod() {
    io.grpc.MethodDescriptor<com.taskengine.proto.CancelJobRequest, com.taskengine.proto.CancelJobResponse> getCancelJobMethod;
    if ((getCancelJobMethod = CoordinatorServiceGrpc.getCancelJobMethod) == null) {
      synchronized (CoordinatorServiceGrpc.class) {
        if ((getCancelJobMethod = CoordinatorServiceGrpc.getCancelJobMethod) == null) {
          CoordinatorServiceGrpc.getCancelJobMethod = getCancelJobMethod =
              io.grpc.MethodDescriptor.<com.taskengine.proto.CancelJobRequest, com.taskengine.proto.CancelJobResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelJob"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.CancelJobRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.taskengine.proto.CancelJobResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CoordinatorServiceMethodDescriptorSupplier("CancelJob"))
              .build();
        }
      }
    }
    return getCancelJobMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CoordinatorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceStub>() {
        @java.lang.Override
        public CoordinatorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CoordinatorServiceStub(channel, callOptions);
        }
      };
    return CoordinatorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static CoordinatorServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceBlockingV2Stub>() {
        @java.lang.Override
        public CoordinatorServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CoordinatorServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return CoordinatorServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CoordinatorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceBlockingStub>() {
        @java.lang.Override
        public CoordinatorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CoordinatorServiceBlockingStub(channel, callOptions);
        }
      };
    return CoordinatorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CoordinatorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CoordinatorServiceFutureStub>() {
        @java.lang.Override
        public CoordinatorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CoordinatorServiceFutureStub(channel, callOptions);
        }
      };
    return CoordinatorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Worker registration — called once when a worker starts up
     * </pre>
     */
    default void registerWorker(com.taskengine.proto.RegisterWorkerRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.RegisterWorkerResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterWorkerMethod(), responseObserver);
    }

    /**
     * <pre>
     * Worker heartbeat — called every 5 seconds by each worker
     * </pre>
     */
    default void heartbeat(com.taskengine.proto.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }

    /**
     * <pre>
     * Job submission — called by clients to submit a new job
     * </pre>
     */
    default void submitJob(com.taskengine.proto.SubmitJobRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.SubmitJobResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitJobMethod(), responseObserver);
    }

    /**
     * <pre>
     * Job status query — called by clients to check job progress
     * </pre>
     */
    default void getJobStatus(com.taskengine.proto.GetJobStatusRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.GetJobStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetJobStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * Task completion report — called by workers when a task succeeds
     * </pre>
     */
    default void reportTaskCompletion(com.taskengine.proto.ReportTaskCompletionRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskCompletionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportTaskCompletionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Task failure report — called by workers when a task fails
     * </pre>
     */
    default void reportTaskFailure(com.taskengine.proto.ReportTaskFailureRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskFailureResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportTaskFailureMethod(), responseObserver);
    }

    /**
     * <pre>
     * Job cancellation — called by clients to cancel a running job
     * </pre>
     */
    default void cancelJob(com.taskengine.proto.CancelJobRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.CancelJobResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelJobMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CoordinatorService.
   */
  public static abstract class CoordinatorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CoordinatorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CoordinatorService.
   */
  public static final class CoordinatorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CoordinatorServiceStub> {
    private CoordinatorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CoordinatorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CoordinatorServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Worker registration — called once when a worker starts up
     * </pre>
     */
    public void registerWorker(com.taskengine.proto.RegisterWorkerRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.RegisterWorkerResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterWorkerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Worker heartbeat — called every 5 seconds by each worker
     * </pre>
     */
    public void heartbeat(com.taskengine.proto.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Job submission — called by clients to submit a new job
     * </pre>
     */
    public void submitJob(com.taskengine.proto.SubmitJobRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.SubmitJobResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitJobMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Job status query — called by clients to check job progress
     * </pre>
     */
    public void getJobStatus(com.taskengine.proto.GetJobStatusRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.GetJobStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetJobStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Task completion report — called by workers when a task succeeds
     * </pre>
     */
    public void reportTaskCompletion(com.taskengine.proto.ReportTaskCompletionRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskCompletionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportTaskCompletionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Task failure report — called by workers when a task fails
     * </pre>
     */
    public void reportTaskFailure(com.taskengine.proto.ReportTaskFailureRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskFailureResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportTaskFailureMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Job cancellation — called by clients to cancel a running job
     * </pre>
     */
    public void cancelJob(com.taskengine.proto.CancelJobRequest request,
        io.grpc.stub.StreamObserver<com.taskengine.proto.CancelJobResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelJobMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CoordinatorService.
   */
  public static final class CoordinatorServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<CoordinatorServiceBlockingV2Stub> {
    private CoordinatorServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CoordinatorServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CoordinatorServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Worker registration — called once when a worker starts up
     * </pre>
     */
    public com.taskengine.proto.RegisterWorkerResponse registerWorker(com.taskengine.proto.RegisterWorkerRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getRegisterWorkerMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Worker heartbeat — called every 5 seconds by each worker
     * </pre>
     */
    public com.taskengine.proto.HeartbeatResponse heartbeat(com.taskengine.proto.HeartbeatRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job submission — called by clients to submit a new job
     * </pre>
     */
    public com.taskengine.proto.SubmitJobResponse submitJob(com.taskengine.proto.SubmitJobRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSubmitJobMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job status query — called by clients to check job progress
     * </pre>
     */
    public com.taskengine.proto.GetJobStatusResponse getJobStatus(com.taskengine.proto.GetJobStatusRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetJobStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task completion report — called by workers when a task succeeds
     * </pre>
     */
    public com.taskengine.proto.ReportTaskCompletionResponse reportTaskCompletion(com.taskengine.proto.ReportTaskCompletionRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getReportTaskCompletionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task failure report — called by workers when a task fails
     * </pre>
     */
    public com.taskengine.proto.ReportTaskFailureResponse reportTaskFailure(com.taskengine.proto.ReportTaskFailureRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getReportTaskFailureMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job cancellation — called by clients to cancel a running job
     * </pre>
     */
    public com.taskengine.proto.CancelJobResponse cancelJob(com.taskengine.proto.CancelJobRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelJobMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service CoordinatorService.
   */
  public static final class CoordinatorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CoordinatorServiceBlockingStub> {
    private CoordinatorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CoordinatorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CoordinatorServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Worker registration — called once when a worker starts up
     * </pre>
     */
    public com.taskengine.proto.RegisterWorkerResponse registerWorker(com.taskengine.proto.RegisterWorkerRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterWorkerMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Worker heartbeat — called every 5 seconds by each worker
     * </pre>
     */
    public com.taskengine.proto.HeartbeatResponse heartbeat(com.taskengine.proto.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job submission — called by clients to submit a new job
     * </pre>
     */
    public com.taskengine.proto.SubmitJobResponse submitJob(com.taskengine.proto.SubmitJobRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitJobMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job status query — called by clients to check job progress
     * </pre>
     */
    public com.taskengine.proto.GetJobStatusResponse getJobStatus(com.taskengine.proto.GetJobStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetJobStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task completion report — called by workers when a task succeeds
     * </pre>
     */
    public com.taskengine.proto.ReportTaskCompletionResponse reportTaskCompletion(com.taskengine.proto.ReportTaskCompletionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportTaskCompletionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Task failure report — called by workers when a task fails
     * </pre>
     */
    public com.taskengine.proto.ReportTaskFailureResponse reportTaskFailure(com.taskengine.proto.ReportTaskFailureRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportTaskFailureMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Job cancellation — called by clients to cancel a running job
     * </pre>
     */
    public com.taskengine.proto.CancelJobResponse cancelJob(com.taskengine.proto.CancelJobRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelJobMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CoordinatorService.
   */
  public static final class CoordinatorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CoordinatorServiceFutureStub> {
    private CoordinatorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CoordinatorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CoordinatorServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Worker registration — called once when a worker starts up
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.RegisterWorkerResponse> registerWorker(
        com.taskengine.proto.RegisterWorkerRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterWorkerMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Worker heartbeat — called every 5 seconds by each worker
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.HeartbeatResponse> heartbeat(
        com.taskengine.proto.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Job submission — called by clients to submit a new job
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.SubmitJobResponse> submitJob(
        com.taskengine.proto.SubmitJobRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitJobMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Job status query — called by clients to check job progress
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.GetJobStatusResponse> getJobStatus(
        com.taskengine.proto.GetJobStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetJobStatusMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Task completion report — called by workers when a task succeeds
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.ReportTaskCompletionResponse> reportTaskCompletion(
        com.taskengine.proto.ReportTaskCompletionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportTaskCompletionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Task failure report — called by workers when a task fails
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.ReportTaskFailureResponse> reportTaskFailure(
        com.taskengine.proto.ReportTaskFailureRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportTaskFailureMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Job cancellation — called by clients to cancel a running job
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.taskengine.proto.CancelJobResponse> cancelJob(
        com.taskengine.proto.CancelJobRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelJobMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_WORKER = 0;
  private static final int METHODID_HEARTBEAT = 1;
  private static final int METHODID_SUBMIT_JOB = 2;
  private static final int METHODID_GET_JOB_STATUS = 3;
  private static final int METHODID_REPORT_TASK_COMPLETION = 4;
  private static final int METHODID_REPORT_TASK_FAILURE = 5;
  private static final int METHODID_CANCEL_JOB = 6;

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
        case METHODID_REGISTER_WORKER:
          serviceImpl.registerWorker((com.taskengine.proto.RegisterWorkerRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.RegisterWorkerResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.taskengine.proto.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.HeartbeatResponse>) responseObserver);
          break;
        case METHODID_SUBMIT_JOB:
          serviceImpl.submitJob((com.taskengine.proto.SubmitJobRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.SubmitJobResponse>) responseObserver);
          break;
        case METHODID_GET_JOB_STATUS:
          serviceImpl.getJobStatus((com.taskengine.proto.GetJobStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.GetJobStatusResponse>) responseObserver);
          break;
        case METHODID_REPORT_TASK_COMPLETION:
          serviceImpl.reportTaskCompletion((com.taskengine.proto.ReportTaskCompletionRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskCompletionResponse>) responseObserver);
          break;
        case METHODID_REPORT_TASK_FAILURE:
          serviceImpl.reportTaskFailure((com.taskengine.proto.ReportTaskFailureRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.ReportTaskFailureResponse>) responseObserver);
          break;
        case METHODID_CANCEL_JOB:
          serviceImpl.cancelJob((com.taskengine.proto.CancelJobRequest) request,
              (io.grpc.stub.StreamObserver<com.taskengine.proto.CancelJobResponse>) responseObserver);
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
          getRegisterWorkerMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.RegisterWorkerRequest,
              com.taskengine.proto.RegisterWorkerResponse>(
                service, METHODID_REGISTER_WORKER)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.HeartbeatRequest,
              com.taskengine.proto.HeartbeatResponse>(
                service, METHODID_HEARTBEAT)))
        .addMethod(
          getSubmitJobMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.SubmitJobRequest,
              com.taskengine.proto.SubmitJobResponse>(
                service, METHODID_SUBMIT_JOB)))
        .addMethod(
          getGetJobStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.GetJobStatusRequest,
              com.taskengine.proto.GetJobStatusResponse>(
                service, METHODID_GET_JOB_STATUS)))
        .addMethod(
          getReportTaskCompletionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.ReportTaskCompletionRequest,
              com.taskengine.proto.ReportTaskCompletionResponse>(
                service, METHODID_REPORT_TASK_COMPLETION)))
        .addMethod(
          getReportTaskFailureMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.ReportTaskFailureRequest,
              com.taskengine.proto.ReportTaskFailureResponse>(
                service, METHODID_REPORT_TASK_FAILURE)))
        .addMethod(
          getCancelJobMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.taskengine.proto.CancelJobRequest,
              com.taskengine.proto.CancelJobResponse>(
                service, METHODID_CANCEL_JOB)))
        .build();
  }

  private static abstract class CoordinatorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CoordinatorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.taskengine.proto.CoordinatorServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CoordinatorService");
    }
  }

  private static final class CoordinatorServiceFileDescriptorSupplier
      extends CoordinatorServiceBaseDescriptorSupplier {
    CoordinatorServiceFileDescriptorSupplier() {}
  }

  private static final class CoordinatorServiceMethodDescriptorSupplier
      extends CoordinatorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CoordinatorServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (CoordinatorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CoordinatorServiceFileDescriptorSupplier())
              .addMethod(getRegisterWorkerMethod())
              .addMethod(getHeartbeatMethod())
              .addMethod(getSubmitJobMethod())
              .addMethod(getGetJobStatusMethod())
              .addMethod(getReportTaskCompletionMethod())
              .addMethod(getReportTaskFailureMethod())
              .addMethod(getCancelJobMethod())
              .build();
        }
      }
    }
    return result;
  }
}
