package com.didan.testperformance.first.config.grpc;

import com.didan.testperformance.first.entity.RequestServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.xds.XdsChannelCredentials;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcClientConfiguration {


  private static final String GZIP_COMPRESSION = "gzip";
  private static final String XDS_SCHEME = "xds:///";

  @Qualifier("ioExecutor")
  private final Executor executor;
  private final GrpcInterceptor grpcInterceptor;
  private final GrpcProperties grpcProperties;

  @Bean
  public RequestServiceGrpc.RequestServiceBlockingStub clientServiceBlockingStub() {
    Channel channel = creatChannel();
    return RequestServiceGrpc.newBlockingStub(channel);
  }

  private Channel creatChannel() {
    if (grpcProperties.isXdsEnabled()) {
      return createProxylessChannel();
    } else {
      return createSidecarChannel();
    }
  }

  private Channel createSidecarChannel() {
    log.info("Creating sidecar gRPC channel");
    return ManagedChannelBuilder.forAddress(grpcProperties.getClients().getHost(), grpcProperties.getClients().getPort())
        .usePlaintext()
        .executor(executor)
        .intercept(grpcInterceptor)
        .intercept(gzipInterceptor())
        .keepAliveTime(grpcProperties.getClients().getKeepAliveTime(), TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .keepAliveTimeout(grpcProperties.getClients().getKeepAliveTimeout(), TimeUnit.SECONDS)
        .disableRetry()
        .build();
  }

  private Channel createProxylessChannel() {
    String target = buildXdsTarget();
    log.info("Creating Proxyless gRPC channel for target: {}", target);
    return Grpc.newChannelBuilder(target, XdsChannelCredentials.create(InsecureChannelCredentials.create()))
        .executor(executor)
        .intercept(grpcInterceptor)
        .intercept(gzipInterceptor())
        .keepAliveTime(grpcProperties.getClients().getKeepAliveTime(), TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .keepAliveTimeout(grpcProperties.getClients().getKeepAliveTimeout(), TimeUnit.SECONDS)
        .defaultLoadBalancingPolicy(grpcProperties.getServers().getLoadBalancingPolicy())
        .disableRetry()
        .build();
  }

  private String buildXdsTarget() {
    return XDS_SCHEME + grpcProperties.getClients().getXdsNameService();
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
