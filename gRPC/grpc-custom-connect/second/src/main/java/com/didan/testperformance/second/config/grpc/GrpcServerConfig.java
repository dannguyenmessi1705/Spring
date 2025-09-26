package com.didan.testperformance.second.config.grpc;

import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GrpcServerConfig implements SmartLifecycle {

  @Value("${grpc.server.port}")
  private int port;

  private final List<BindableService> grpcServices; // Tự động inject tất cả các service xử lý request gRPC vào đây
  private final GrpcServerInterceptor grpcServerInterceptor; // Interceptor để xử lý các logic chung như logging, authentication, v.v.
  private final Executor ioExecutor; // Executor để xử lý các tác vụ I/O

  private Server server; // Đối tượng server gRPC
  private volatile boolean running = false; // Trạng thái chạy của server

  @Override
  public void start() {
    log.info("starting grpc server");
    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port); // Tạo builder cho server gRPC trên cổng được cấu hình

    for (BindableService service : grpcServices) { // Đăng ký từng service với interceptor
      serverBuilder.addService(ServerInterceptors.intercept(service, grpcServerInterceptor)); // Thêm interceptor vào mỗi service
    }

    try {
      server = serverBuilder // Cấu hình và khởi động server
          .compressorRegistry(CompressorRegistry.getDefaultInstance()) // Hỗ trợ nén dữ liệu
          .decompressorRegistry(DecompressorRegistry.getDefaultInstance()) // Hỗ trợ giải nén dữ liệu
          .keepAliveTime(10L, TimeUnit.SECONDS) // Cấu hình keep-alive để giữ kết nối
          .keepAliveTimeout(10L, TimeUnit.SECONDS) // Thời gian chờ keep-alive
          .permitKeepAliveTime(5L, TimeUnit.SECONDS) // Cho phép keep-alive sau khoảng thời gian này
          .permitKeepAliveWithoutCalls(true) // Cho phép keep-alive ngay cả khi không có cuộc gọi
          .maxInboundMessageSize(4 * 1024 * 1024) // 4MB giới hạn kích thước tin nhắn đến
          .maxInboundMetadataSize(8 * 1024) // 8KB giới hạn kích thước metadata đến
          .executor(ioExecutor) // Sử dụng executor tùy chỉnh để xử lý I/O
          .build()
          .start(); // Khởi động server
    } catch (IOException e) {
      log.error("failed to start grpc server", e);
      throw new RuntimeException("Failed to start gRPC server", e);
    }

    running = true;
    log.info("grpc server started on port {} with {} services", port, grpcServices.size());
  }

  @Override
  public void stop() {
    if (server != null && !server.isShutdown()) { // Kiểm tra nếu server đang chạy
      log.info("shutting down gRPC server");
      try {
        server.shutdown().awaitTermination(30, TimeUnit.SECONDS); // Tắt server và chờ tối đa 30 giây để hoàn tất các tác vụ hiện tại
        running = false;
        log.info("gRPC server shut down successfully");
      } catch (InterruptedException e) {
        log.error("gRPC server shutdown interrupted", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE; // Chạy bắt đầu cuối cùng, dừng đầu tiên
  }
}
