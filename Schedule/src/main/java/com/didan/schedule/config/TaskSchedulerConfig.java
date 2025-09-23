package com.didan.schedule.config;

// Import Lombok annotation để tự động tạo logger
import lombok.extern.slf4j.Slf4j;
// Import Bean annotation để đánh dấu phương thức tạo bean
import org.springframework.context.annotation.Bean;
// Import Configuration để đánh dấu class này là configuration class
import org.springframework.context.annotation.Configuration;
// Import interface TaskScheduler - interface chung cho việc lên lịch task
import org.springframework.scheduling.TaskScheduler;
// Import implementation cụ thể của TaskScheduler sử dụng thread pool
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

// Annotation đánh dấu đây là class cấu hình Spring
@Configuration
// Annotation Lombok để tự động tạo logger với tên là log
@Slf4j
public class TaskSchedulerConfig {

  // Bean tạo TaskScheduler để lên lịch các task theo thời gian
  @Bean
  public TaskScheduler taskScheduler() {
    // Tạo instance của ThreadPoolTaskScheduler - implementation cụ thể của TaskScheduler
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    // Thiết lập kích thước pool = 8 thread
    // Khác với TaskExecutor, TaskScheduler chỉ có một pool size duy nhất
    // Tất cả 8 thread có thể được sử dụng để chạy scheduled task
    taskScheduler.setPoolSize(8);

    // Đặt prefix cho tên thread = "TaskScheduler-"
    // Các thread sẽ có tên dạng: TaskScheduler-1, TaskScheduler-2, ...
    taskScheduler.setThreadNamePrefix("TaskScheduler-");

    // Chờ các task hoàn thành trước khi shutdown ứng dụng
    // Đảm bảo không mất scheduled task khi application stop
    taskScheduler.setWaitForTasksToCompleteOnShutdown(true);

    // Set task decorator để copy MDC (Mapped Diagnostic Context) giữa các thread
    // Giúp maintain logging context trong scheduled task execution
    taskScheduler.setTaskDecorator(new MdcTaskDecorator());

    // Thiết lập handler xử lý khi task bị reject (khi pool đầy)
    taskScheduler.setRejectedExecutionHandler((runnable, exec) -> {
      try {
        // Kiểm tra executor chưa shutdown
        if (!exec.isShutdown()) {
          // Thử đưa task vào queue một lần nữa (blocking)
          // put() sẽ chờ đến khi có chỗ trống trong queue
          exec.getQueue().put(runnable);

          // Log debug thông tin về việc retry scheduled task
          log.debug("Task scheduler rejected, retrying. ActiveCount: {}, CompletedCount: {}, Queue: {}",
              exec.getActiveCount(), // Số thread đang active
              exec.getCompletedTaskCount(), // Số task đã hoàn thành
              exec.getQueue().size()); // Số task trong queue
        } else {
          // Nếu scheduler đang shutdown, không thể queue task
          log.error("Task scheduler is shutting down, cannot queue task.");
        }
      } catch (InterruptedException ex) {
        // Restore interrupt status cho thread hiện tại khi bị interrupt
        Thread.currentThread().interrupt();
        // Log error khi scheduler đang shutdown
        log.error("Task scheduler is shutting down, cannot queue task.");
      }
    });

    // Khởi tạo scheduler (tạo các thread trong pool)
    taskScheduler.initialize();
    return taskScheduler;
  }

  // Phương thức static để xử lý task scheduler - kiểm tra và điều chỉnh pool
  public static void handlerTask(TaskScheduler scheduler, int maxPoolSize) {
    try {
      // Kiểm tra scheduler có phải là ThreadPoolTaskScheduler không
      // Sử dụng pattern matching (Java 14+) thay vì instanceof + cast
      if (scheduler instanceof ThreadPoolTaskScheduler threadPool) {

        // Log thông tin về pool size hiện tại và yêu cầu
        log.info("Handling task. Current max pool size: {}, Requested max pool size: {}",
            threadPool.getPoolSize(), // Pool size hiện tại
            maxPoolSize); // Pool size được yêu cầu

        // Lấy số thread đang active (đang xử lý scheduled task)
        int activeThread = threadPool.getActiveCount();

        // Kiểm tra nếu số thread active >= maxPoolSize yêu cầu
        if (activeThread >= maxPoolSize) {
          // Log warning khi vượt quá giới hạn
          log.warn("Current max pool size exceeded, waiting task.");
          // Sleep ngắn để giảm tải và chờ một số scheduled task hoàn thành
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
