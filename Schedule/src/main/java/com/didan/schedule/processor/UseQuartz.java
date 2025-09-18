package com.didan.schedule.processor;

// Import config để xử lý TaskExecutor
import com.didan.schedule.config.TaskExecutorConfig;
// Import service để gọi business logic
import com.didan.schedule.service.UserService;
// Import utility class chứa static data
import com.didan.schedule.utils.Data;
// Import Lombok annotation để tự động tạo logger
import lombok.extern.slf4j.Slf4j;
// Import annotation của Quartz để ngăn chặn việc thực thi đồng thời
import org.quartz.DisallowConcurrentExecution;
// Import context chứa thông tin về job execution
import org.quartz.JobExecutionContext;
// Import Spring annotation để inject dependency
import org.springframework.beans.factory.annotation.Autowired;
// Import interface TaskExecutor để thực thi task bất đồng bộ
import org.springframework.core.task.TaskExecutor;
// Import Spring annotation để đánh dấu component
import org.springframework.stereotype.Component;

// Annotation Lombok để tự động tạo logger với tên là log
@Slf4j
// Annotation Spring để đánh dấu đây là component, được quản lý bởi Spring container
@Component
// Annotation Quartz để ngăn chặn việc thực thi đồng thời của cùng một job
// Nếu job đang chạy và chưa hoàn thành, trigger tiếp theo sẽ bị bỏ qua
// Điều này đảm bảo chỉ có một instance của job chạy tại một thời điểm
@DisallowConcurrentExecution
public class UseQuartz extends QuartzContextJob {

  // Inject UserService để thực hiện business logic
  @Autowired
  private UserService userService;

  // Inject TaskExecutor (thread pool) để th��c thi task bất đồng bộ
  @Autowired
  private TaskExecutor threadPoolTaskExecutor;

  // Override phương thức run từ QuartzContextJob để định nghĩa logic job
  @Override
  public void run(JobExecutionContext context) {
    try {
      // Gọi utility method để kiểm tra và điều chỉnh thread pool
      // Tham số 8 là maxPoolSize - giới hạn số thread tối đa
      // Nếu số active thread >= 8, sẽ sleep 200ms để chờ
      TaskExecutorConfig.handlerTask(threadPoolTaskExecutor, 8);

      // Submit task vào thread pool để thực thi bất đồng bộ
      threadPoolTaskExecutor.execute(() -> {
        // Log thông tin job đang chạy với tên job và index tăng dần
        // Data.BEGIN_IDX++ - counter static để đếm số lần job chạy
        log.info("Quartz {} is running every 5 seconds, with IDX: [{}]",
            context.getJobDetail().getKey().getName(), // Tên của job
            Data.BEGIN_IDX++); // Index tăng dần mỗi lần chạy

        // Gọi UserService để thực hiện business logic chính
        userService.getUsers();
      });
    } catch (Exception ex) {
      // Bắt và log mọi exception xảy ra trong quá trình thực thi job
      log.error("Error in Quartz", ex);
    } finally {
      // Block finally luôn được thực thi, dù có exception hay không
      // Log thông báo job đã hoàn thành
      log.info("Quartz job {} is done", context.getJobDetail().getKey().getName());
    }
  }
}
