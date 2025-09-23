package com.didan.isosocketconnection.serversocket.service;

import com.didan.isosocketconnection.serversocket.decoder.IsoMessageDecoder;
import com.didan.isosocketconnection.serversocket.handler.IsoMessageHandler;
import com.didan.isosocketconnection.serversocket.util.ConnectorUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NettyServerService {

  @Value("${netty.socket.server.port:9090}")
  private int port;

  @Value("${netty.socket.server.host:localhost}")
  private String host;

  @Value("${netty.socket.server.boss-threads:1}")
  private int bossThreads;

  @Value("${netty.socket.server.worker-threads:4}")
  private int workerThreads;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private Channel serverChannel;
  private GenericPackager isoPackager;

  /**
   * Hàm khởi động server Netty và nạp cấu hình ISO8583
   */
  @PostConstruct
  public void startServer() {
    try {
      // Load ISO packager configuration
      isoPackager = ConnectorUtils.readGenericManager("isodef.xml"); // Doc file cau hinh GenericPackager tu duong dan trong classpath
      log.info("ISO packager loaded successfully");

      // Initialize Netty server
      bossGroup = new NioEventLoopGroup(bossThreads); // Cấp phát sự kiện cho các kết nối mới
      workerGroup = new NioEventLoopGroup(workerThreads); // Cấp phát sự kiện cho các kết nối đã được chấp nhận

      ServerBootstrap bootstrap = new ServerBootstrap(); // Khởi tạo bootstrap cho server
      bootstrap.group(bossGroup, workerGroup) // Thiết lập nhóm sự kiện cho boss và worker
          .channel(NioServerSocketChannel.class) // Sử dụng kênh NIO cho server
          .option(ChannelOption.SO_BACKLOG, 1024) // Thiết lập kích thước hàng đợi kết nối
          .option(ChannelOption.SO_REUSEADDR, true) // Cho phép tái sử dụng địa chỉ
          .childOption(ChannelOption.SO_KEEPALIVE, true) // Giữ kết nối sống
          .childOption(ChannelOption.TCP_NODELAY, true) // Vô hiệu hóa Nagle's algorithm
          .childOption(ChannelOption.SO_RCVBUF, 64 * 1024) // Thiết lập kích thước bộ đệm nhận
          .childOption(ChannelOption.SO_SNDBUF, 64 * 1024) // Thiết lập kích thước bộ đệm gửi
          .handler(new LoggingHandler(LogLevel.INFO)) // Thêm bộ xử lý ghi log
          .childHandler(new ChannelInitializer<SocketChannel>() { // Khởi tạo kênh con cho mỗi kết nối mới
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline(); // Lấy pipeline của kênh

              // Add idle state handler for connection management
              pipeline.addLast("idleStateHandler",
                  new IdleStateHandler(300, 300, 600, TimeUnit.SECONDS)); // Thêm bộ xử lý trạng thái nhàn rỗi để quản lý kết nối

              // Add custom handlers
              pipeline.addLast("decoder", new IsoMessageDecoder(isoPackager)); // Thêm bộ giải mã thông điệp ISO
              pipeline.addLast("handler", new IsoMessageHandler(isoPackager)); // Thêm bộ xử lý thông điệp ISO
            }
          });

      // Bind and start to accept incoming connections
      ChannelFuture future = bootstrap.bind(host, port).sync(); // Ràng buộc server với host và port đã cấu hình
      serverChannel = future.channel(); // Lấy kênh server từ tương lai

      log.info("ISO Socket Server started successfully on {}:{}", host, port);
      log.info("Server is ready to accept ISO8583 connections");

    } catch (Exception e) {
      log.error("Failed to start ISO Socket Server", e);
      shutdown();
      throw new RuntimeException("Failed to start server", e);
    }
  }

  /**
   * Hàm tắt server Netty và giải phóng tài nguyên
   */
  @PreDestroy
  public void shutdown() {
    log.info("Shutting down ISO Socket Server...");

    try {
      if (serverChannel != null && serverChannel.isActive()) { // Kiểm tra nếu kênh server đang hoạt động
        serverChannel.close().sync(); // Đóng kênh server và chờ cho đến khi hoàn thành
      }
    } catch (InterruptedException e) {
      log.error("Error closing server channel", e);
      Thread.currentThread().interrupt();
    }

    if (workerGroup != null) { // Kiểm tra nếu nhóm worker không null
      workerGroup.shutdownGracefully(); // Tắt nhóm worker một cách nhẹ nhàng
    }
    if (bossGroup != null) { // Kiểm tra nếu nhóm boss không null
      bossGroup.shutdownGracefully(); // Tắt nhóm boss một cách nhẹ nhàng
    }

    log.info("ISO Socket Server shutdown completed");
  }

  /**
   * Check if server is running
   */
  public boolean isRunning() {
    return serverChannel != null && serverChannel.isActive();
  }

  /**
   * Get server information
   */
  public String getServerInfo() {
    if (isRunning()) {
      return String.format("ISO Socket Server running on %s:%d", host, port);
    } else {
      return "ISO Socket Server is not running";
    }
  }
}
