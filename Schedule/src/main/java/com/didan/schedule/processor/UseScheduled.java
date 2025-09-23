package com.didan.schedule.processor;

import com.didan.schedule.config.TaskExecutorConfig;
import com.didan.schedule.service.UserService;
import com.didan.schedule.utils.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UseScheduled {

  private final UserService userService;
  private final TaskExecutor threadPoolTaskExecutor;

  @Scheduled(fixedRate = 5000)
  public void scheduled() {
    try {
      TaskExecutorConfig.handlerTask(threadPoolTaskExecutor, 8);
      threadPoolTaskExecutor.execute(() -> {
        log.info("@Scheduled is running every 5 seconds, with IDX: [{}]", Data.BEGIN_IDX++);
        userService.getUsers();
      });
    } catch (Exception ex) {
      log.error("Error in @Scheduled", ex);
    } finally {
      log.info("@Scheduled is done");
    }
  }
}
