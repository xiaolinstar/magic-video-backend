package cn.xiaolin.api.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 用户权限gRPC接口
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.57.1)",
    comments = "Source: UserPerm.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserPermServiceGrpc {

  private UserPermServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "UserPermService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Int64Value,
      cn.xiaolin.api.grpc.UserPermProto.PermsResponse> getGetPermByUserIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getPermByUserId",
      requestType = com.google.protobuf.Int64Value.class,
      responseType = cn.xiaolin.api.grpc.UserPermProto.PermsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Int64Value,
      cn.xiaolin.api.grpc.UserPermProto.PermsResponse> getGetPermByUserIdMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Int64Value, cn.xiaolin.api.grpc.UserPermProto.PermsResponse> getGetPermByUserIdMethod;
    if ((getGetPermByUserIdMethod = UserPermServiceGrpc.getGetPermByUserIdMethod) == null) {
      synchronized (UserPermServiceGrpc.class) {
        if ((getGetPermByUserIdMethod = UserPermServiceGrpc.getGetPermByUserIdMethod) == null) {
          UserPermServiceGrpc.getGetPermByUserIdMethod = getGetPermByUserIdMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Int64Value, cn.xiaolin.api.grpc.UserPermProto.PermsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getPermByUserId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Int64Value.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cn.xiaolin.api.grpc.UserPermProto.PermsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserPermServiceMethodDescriptorSupplier("getPermByUserId"))
              .build();
        }
      }
    }
    return getGetPermByUserIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Int64Value,
      cn.xiaolin.api.grpc.UserPermProto.RolesResponse> getGetRoleByUserIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getRoleByUserId",
      requestType = com.google.protobuf.Int64Value.class,
      responseType = cn.xiaolin.api.grpc.UserPermProto.RolesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Int64Value,
      cn.xiaolin.api.grpc.UserPermProto.RolesResponse> getGetRoleByUserIdMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Int64Value, cn.xiaolin.api.grpc.UserPermProto.RolesResponse> getGetRoleByUserIdMethod;
    if ((getGetRoleByUserIdMethod = UserPermServiceGrpc.getGetRoleByUserIdMethod) == null) {
      synchronized (UserPermServiceGrpc.class) {
        if ((getGetRoleByUserIdMethod = UserPermServiceGrpc.getGetRoleByUserIdMethod) == null) {
          UserPermServiceGrpc.getGetRoleByUserIdMethod = getGetRoleByUserIdMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Int64Value, cn.xiaolin.api.grpc.UserPermProto.RolesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getRoleByUserId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Int64Value.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cn.xiaolin.api.grpc.UserPermProto.RolesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserPermServiceMethodDescriptorSupplier("getRoleByUserId"))
              .build();
        }
      }
    }
    return getGetRoleByUserIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserPermServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserPermServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserPermServiceStub>() {
        @java.lang.Override
        public UserPermServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserPermServiceStub(channel, callOptions);
        }
      };
    return UserPermServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserPermServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserPermServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserPermServiceBlockingStub>() {
        @java.lang.Override
        public UserPermServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserPermServiceBlockingStub(channel, callOptions);
        }
      };
    return UserPermServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserPermServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserPermServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserPermServiceFutureStub>() {
        @java.lang.Override
        public UserPermServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserPermServiceFutureStub(channel, callOptions);
        }
      };
    return UserPermServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 用户权限gRPC接口
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void getPermByUserId(com.google.protobuf.Int64Value request,
        io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.PermsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPermByUserIdMethod(), responseObserver);
    }

    /**
     */
    default void getRoleByUserId(com.google.protobuf.Int64Value request,
        io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.RolesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetRoleByUserIdMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UserPermService.
   * <pre>
   * 用户权限gRPC接口
   * </pre>
   */
  public static abstract class UserPermServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UserPermServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UserPermService.
   * <pre>
   * 用户权限gRPC接口
   * </pre>
   */
  public static final class UserPermServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UserPermServiceStub> {
    private UserPermServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserPermServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserPermServiceStub(channel, callOptions);
    }

    /**
     */
    public void getPermByUserId(com.google.protobuf.Int64Value request,
        io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.PermsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPermByUserIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getRoleByUserId(com.google.protobuf.Int64Value request,
        io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.RolesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetRoleByUserIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UserPermService.
   * <pre>
   * 用户权限gRPC接口
   * </pre>
   */
  public static final class UserPermServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UserPermServiceBlockingStub> {
    private UserPermServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserPermServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserPermServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public cn.xiaolin.api.grpc.UserPermProto.PermsResponse getPermByUserId(com.google.protobuf.Int64Value request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPermByUserIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public cn.xiaolin.api.grpc.UserPermProto.RolesResponse getRoleByUserId(com.google.protobuf.Int64Value request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetRoleByUserIdMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UserPermService.
   * <pre>
   * 用户权限gRPC接口
   * </pre>
   */
  public static final class UserPermServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UserPermServiceFutureStub> {
    private UserPermServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserPermServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserPermServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cn.xiaolin.api.grpc.UserPermProto.PermsResponse> getPermByUserId(
        com.google.protobuf.Int64Value request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPermByUserIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cn.xiaolin.api.grpc.UserPermProto.RolesResponse> getRoleByUserId(
        com.google.protobuf.Int64Value request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetRoleByUserIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PERM_BY_USER_ID = 0;
  private static final int METHODID_GET_ROLE_BY_USER_ID = 1;

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
        case METHODID_GET_PERM_BY_USER_ID:
          serviceImpl.getPermByUserId((com.google.protobuf.Int64Value) request,
              (io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.PermsResponse>) responseObserver);
          break;
        case METHODID_GET_ROLE_BY_USER_ID:
          serviceImpl.getRoleByUserId((com.google.protobuf.Int64Value) request,
              (io.grpc.stub.StreamObserver<cn.xiaolin.api.grpc.UserPermProto.RolesResponse>) responseObserver);
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
          getGetPermByUserIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.protobuf.Int64Value,
              cn.xiaolin.api.grpc.UserPermProto.PermsResponse>(
                service, METHODID_GET_PERM_BY_USER_ID)))
        .addMethod(
          getGetRoleByUserIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.protobuf.Int64Value,
              cn.xiaolin.api.grpc.UserPermProto.RolesResponse>(
                service, METHODID_GET_ROLE_BY_USER_ID)))
        .build();
  }

  private static abstract class UserPermServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserPermServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return cn.xiaolin.api.grpc.UserPermProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserPermService");
    }
  }

  private static final class UserPermServiceFileDescriptorSupplier
      extends UserPermServiceBaseDescriptorSupplier {
    UserPermServiceFileDescriptorSupplier() {}
  }

  private static final class UserPermServiceMethodDescriptorSupplier
      extends UserPermServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UserPermServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (UserPermServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserPermServiceFileDescriptorSupplier())
              .addMethod(getGetPermByUserIdMethod())
              .addMethod(getGetRoleByUserIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
