package com.didan.testperformance.first.config.grpc;

import io.grpc.BindableService;
import io.grpc.DecompressorRegistry;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.xds.XdsServerBuilder;
import io.grpc.xds.XdsServerCredentials;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
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
public class GrpcServerConfiguration {

  private Server server;
  private final ServerTracingInterceptor serverTracingInterceptor;
  private final GrpcProperties grpcProperties;
  private final HealthStatusManager healthStatusManager = new HealthStatusManager();

  @Bean
  public HealthStatusManager healthStatusManager() {
    return healthStatusManager;
  }

  @Bean
  public Server grpcServer(GrpcProperties grpcProperties, List<BindableService> grpcServices, @Qualifier("ioExecutor") Executor executor) {
    try {
      if (grpcProperties.isXdsEnabled()) {
        log.info("Starting gRPC server with xDS support on port {}", grpcProperties.getServers().getPort());
        server = createXdsServer(grpcProperties, grpcServices, executor);
      } else {
        log.info("Starting gRPC server with Sidecar on port {}", grpcProperties.getServers().getPort());
        server = createSidecarServer(grpcProperties, grpcServices, executor);
      }
      server.start();
    } catch (IOException e) {
      log.error("Failed to start gRPC server", e);
      throw new RuntimeException("Failed to start gRPC server", e);
    }
    log.info("gRPC server started successfully on port {}", grpcProperties.getServers().getPort());
    healthStatusManager.setStatus("", ServingStatus.SERVING);
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutDownServer));
    return server;
  }

  @PreDestroy
  public void stopServer() {
    shutDownServer();
  }

  private void shutDownServer() {
    if (server != null && !server.isShutdown()) {
      log.info("Shutting down gRPC server since JVM is shutting down");
      try {
        server.shutdown();
        if (!server.awaitTermination(grpcProperties.getServers().getAwaitTermination(), TimeUnit.MILLISECONDS)) {
          log.warn("gRPC server did not shut down in the allocated time. Forcing shutdown.");
          server.shutdownNow();
        }
        log.info("gRPC server shut down successfully");
        healthStatusManager.setStatus(null, ServingStatus.NOT_SERVING);
      } catch (InterruptedException e) {
        log.error("gRPC server shut down interrupted", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  private Server createXdsServer(GrpcProperties grpcProperties, List<BindableService> grpcServices, Executor executor) {
    XdsServerBuilder xdsServerBuilder = XdsServerBuilder
        .forPort(grpcProperties.getServers().getPort(), XdsServerCredentials.create(InsecureServerCredentials.create()))
        .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
        .keepAliveTime(grpcProperties.getServers().getKeepAliveTime(), TimeUnit.SECONDS)
        .keepAliveTimeout(grpcProperties.getServers().getKeepAliveTimeout(), TimeUnit.SECONDS)
        .permitKeepAliveWithoutCalls(grpcProperties.getServers().isKeepAliveWithoutCalls())
        .permitKeepAliveTime(grpcProperties.getServers().getPermitKeepAliveTime(), TimeUnit.SECONDS)
        .handshakeTimeout(grpcProperties.getServers().getHandshakeTimeout(), TimeUnit.SECONDS)
        .maxInboundMessageSize(grpcProperties.getServers().getMaxInboundMessageSize())
        .maxInboundMetadataSize(grpcProperties.getServers().getMaxInboundMetadataSize())
        .executor(executor)
        .intercept(serverTracingInterceptor);

    grpcServices.forEach(xdsServerBuilder::addService);
    return xdsServerBuilder.build();
  }

  private Server createSidecarServer(GrpcProperties grpcProperties, List<BindableService> grpcServices, Executor executor) {
    ServerBuilder<?> serverBuilder = ServerBuilder
        .forPort(grpcProperties.getServers().getPort())
        .keepAliveTime(grpcProperties.getServers().getKeepAliveTime(), TimeUnit.SECONDS)
        .keepAliveTimeout(grpcProperties.getServers().getKeepAliveTimeout(), TimeUnit.SECONDS)
        .permitKeepAliveWithoutCalls(grpcProperties.getServers().isKeepAliveWithoutCalls())
        .permitKeepAliveTime(grpcProperties.getServers().getPermitKeepAliveTime(), TimeUnit.SECONDS)
        .handshakeTimeout(grpcProperties.getServers().getHandshakeTimeout(), TimeUnit.SECONDS)
        .maxInboundMessageSize(grpcProperties.getServers().getMaxInboundMessageSize())
        .maxInboundMetadataSize(grpcProperties.getServers().getMaxInboundMetadataSize())
        .executor(executor)
        .intercept(serverTracingInterceptor);

    grpcServices.forEach(serverBuilder::addService);
    return serverBuilder.build();
  }
}
