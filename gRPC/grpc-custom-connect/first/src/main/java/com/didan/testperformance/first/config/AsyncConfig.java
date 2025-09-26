package com.didan.testperformance.first.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
  @Bean("ioExecutor")
  public Executor ioExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    int core = Runtime.getRuntime().availableProcessors(); // lấy số lõi CPU
    executor.setCorePoolSize(core * 2); // số luồng tối thiểu là 2 lần số lõi CPU
    executor.setMaxPoolSize(core * 4); // số luồng tối đa là 4 lần số lõi CPU
    executor.setQueueCapacity(500); // sức chứa hàng đợi
    executor.setThreadNamePrefix("io-executor-grpc-"); // tiền tố tên luồng
    executor.setAllowCoreThreadTimeOut(true); // cho phép luồng lõi hết thời gian chờ
    executor.setKeepAliveSeconds(60); // thời gian chờ trước khi kết thúc luồng không hoạt động
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // chính sách từ chối
    executor.initialize();
    return executor;
  }
}
