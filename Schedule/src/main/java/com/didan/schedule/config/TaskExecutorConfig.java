package com.didan.schedule.config;

// Import Lombok annotation để tự động tạo logger
import lombok.extern.slf4j.Slf4j;
// Import Bean annotation để đánh dấu phương thức tạo bean
import org.springframework.context.annotation.Bean;
// Import Configuration để đánh dấu class này là configuration class
import org.springframework.context.annotation.Configuration;
// Import interface TaskExecutor - interface chung cho việc thực thi task bất đồng bộ
import org.springframework.core.task.TaskExecutor;
// Import implementation cụ thể của TaskExecutor sử dụng thread pool
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// Annotation đánh dấu đây là class cấu hình Spring
@Configuration
// Annotation Lombok để tự động tạo logger với tên là log
@Slf4j
public class TaskExecutorConfig {

  // Bean tạo TaskExecutor với tên "threadPoolTaskExecutor"
  @Bean({"threadPoolTaskExecutor"})
  public TaskExecutor getAsyncExecutor() {
    // Log thông báo bắt đầu tạo Async Task Executor
    log.info("Creating Async Task Executor");

    // Tạo instance của ThreadPoolTaskExecutor - implementation cụ thể của TaskExecutor
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // Thiết lập số thread cơ bản (core) = 5
    // Đây là số thread tối thiểu luôn được giữ trong pool, ngay cả khi idle
    executor.setCorePoolSize(5);

    // Thiết lập số thread tối đa = 8
    // Pool có thể mở rộng đến 8 thread khi có nhiều task chờ xử lý
    executor.setMaxPoolSize(8);

    // Thiết lập kích thước queue = 20
    // Khi tất cả core thread bận, task mới sẽ được đưa vào queue chờ
    // Chỉ khi queue đầy, pool mới tạo thêm thread (đến maxPoolSize)
    executor.setQueueCapacity(20);

    // Đặt prefix cho tên thread = "Async-"
    // Các thread sẽ có tên dạng: Async-1, Async-2, ...
    executor.setThreadNamePrefix("Async-");

    // Chờ các task hoàn thành trước khi shutdown ứng dụng
    // Đảm bảo không mất task khi application stop
    executor.setWaitForTasksToCompleteOnShutdown(true);

    // Thiết lập thời gian sống của thread idle = 30 giây
    // Thread vượt quá core size sẽ bị terminate sau 30s không hoạt động
    executor.setKeepAliveSeconds(30);

    // Set task decorator để copy MDC (Mapped Diagnostic Context) giữa các thread
    // Giúp maintain logging context trong async execution
    executor.setTaskDecorator(new MdcTaskDecorator());

    // Thiết lập handler xử lý khi task bị reject (khi pool và queue đều đầy)
    executor.setRejectedExecutionHandler((runnable, exec) -> {
      try {
        // Kiểm tra executor chưa shutdown
        if (!exec.isShutdown()) {
          // Thử đưa task vào queue một lần nữa (blocking)
          // put() sẽ chờ đến khi có chỗ trống trong queue
          exec.getQueue().put(runnable);

          // Log debug thông tin về việc retry task
          log.debug("Task rejected, retrying. ActiveCount: {}, CompletedCount: {}, Queue: {}",
              exec.getActiveCount(), // Số thread đang active
              exec.getCompletedTaskCount(), // Số task đã hoàn thành
              exec.getQueue().size()); // Số task trong queue
        } else {
          // Nếu executor đang shutdown, không thể queue task
          log.error("Executor is shutting down, cannot queue task.");
        }
      } catch (InterruptedException ex) {
        // Xử lý exception khi bị interrupt trong quá trình put vào queue
        log.error("Task rejected exception {}", ex.getMessage());
        // Restore interrupt status cho thread hiện tại
        Thread.currentThread().interrupt();
      }
    });

    // Khởi tạo executor (tạo core threads)
    executor.initialize();
    return executor;
  }

  // Phương thức static để xử lý task - kiểm tra và điều chỉnh thread pool
  public static void handlerTask(TaskExecutor executor, int maxPoolSize) {
    try {
      // Kiểm tra executor có phải là ThreadPoolTaskExecutor không
      if (executor instanceof ThreadPoolTaskExecutor) {
        // Cast về ThreadPoolTaskExecutor để truy cập các method cụ thể
        ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) executor;

        // Log thông tin về pool size hiện tại và yêu cầu
        log.info("Handling task. Current max pool size: {}, Requested max pool size: {}",
            threadPool.getMaxPoolSize(), // Max pool size hiện tại
            maxPoolSize); // Max pool size được yêu cầu

        // Lấy số thread đang active (đang xử lý task)
        int activeThread = threadPool.getActiveCount();

        // Kiểm tra nếu số thread active >= maxPoolSize yêu cầu
        if (activeThread >= maxPoolSize) {
          // Log warning và sleep 200ms để chờ
          log.warn("Current max pool size exceeded, waiting task.");
          // Sleep ngắn để giảm tải và chờ một số task hoàn thành
          Thread.sleep(200);
        }
      }
    } catch (InterruptedException ex) {
      // Xử lý exception khi bị interrupt trong quá trình sleep
      log.error("Handler task exception {}", ex.getMessage());
      // Restore interrupt status cho thread hiện tại
      Thread.currentThread().interrupt();
    }
  }

}
