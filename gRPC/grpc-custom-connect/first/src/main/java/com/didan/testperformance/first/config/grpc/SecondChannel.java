package com.didan.testperformance.first.config.grpc;

import com.didan.testperformance.first.entity.RequestServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecondChannel {

  private static final String GZIP_COMPRESSION = "gzip";

  private final Executor executor;
  private final GrpcInterceptor grpcInterceptor;

  public SecondChannel(@Qualifier("ioExecutor") Executor executor, GrpcInterceptor grpcInterceptor) {
    this.executor = executor;
    this.grpcInterceptor = grpcInterceptor;
  }

  @Bean
  public RequestServiceGrpc.RequestServiceBlockingStub secondServiceBlockingStub() {
    Channel channel = creatChannel();
    return RequestServiceGrpc.newBlockingStub(channel);
  }

  private Channel creatChannel() {
    log.info("Creating channel to second gRPC server");
    return ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()
        .executor(executor)
        .intercept(grpcInterceptor)
        .intercept(gzipInterceptor())
        .keepAliveTime(10L, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .keepAliveTimeout(10L, TimeUnit.SECONDS)
        .disableRetry()
        .build();
  }

  private ClientInterceptor gzipInterceptor() {
    return new ClientInterceptor() {
      @Override
      public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return channel.newCall(methodDescriptor, callOptions.withCompression(GZIP_COMPRESSION));
      }
    };
  } // Hàm interceptor dùng để nén dữ liệu gửi đi qua gRPC
}
