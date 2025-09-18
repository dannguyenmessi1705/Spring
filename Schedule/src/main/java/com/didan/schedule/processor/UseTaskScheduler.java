package com.didan.schedule.processor;


import com.didan.schedule.config.TaskExecutorConfig;
import com.didan.schedule.config.TaskSchedulerConfig;
import com.didan.schedule.service.UserService;
import com.didan.schedule.utils.Data;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UseTaskScheduler implements InitializingBean {

  private final UserService userService;
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
  private final TaskScheduler taskScheduler;

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      TaskExecutorConfig.handlerTask(threadPoolTaskExecutor, 8);
      threadPoolTaskExecutor.execute(() -> {
        TaskSchedulerConfig.handlerTask(taskScheduler, 8);
        taskScheduler.scheduleAtFixedRate(() -> {
          log.info("Task scheduler is running every 5 seconds, with IDX: [{}]", Data.BEGIN_IDX++);
          userService.getUsers();
        }, Duration.ofSeconds(5));
      });
    } catch (Exception ex) {
      log.error("Error in Task scheduler", ex);
    } finally {
      log.info("Task scheduler is done");
    }
  }
}
