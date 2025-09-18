package com.didan.schedule.config;

// Import các class cần thiết cho Quartz scheduler

import com.didan.schedule.utils.CommonUtils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

// Annotation để đánh dấu đây là class cấu hình Spring
@Configuration
// Annotation để tự động tạo logger với tên là log
@Slf4j
// Annotation Lombok để tự động tạo constructor với các final field
@RequiredArgsConstructor
public class QuartzConfig {

  // Dependency injection: Config chứa danh sách các job được phép chạy
  private final QuartzRunningJobConfig quartzRunningJobConfig;
  // Dependency injection: ApplicationContext để truy cập Spring container
  private final ApplicationContext applicationContext;

  // Bean tạo SpringBeanJobFactory với khả năng autowire
  @Bean
  public SpringBeanJobFactory springBeanJobFactory() {
    // Tạo instance của custom job factory có khả năng autowire
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    // Set ApplicationContext để job factory có thể autowire các bean
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  // Bean tạo SchedulerFactoryBean - trái tim của Quartz scheduler
  @Bean
  public SchedulerFactoryBean scheduler(Trigger... triggers) {
    // Tạo instance của SchedulerFactoryBean
    SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

    // Tạo properties để cấu hình Quartz scheduler
    Properties properties = new Properties();
    // Đặt tên cho scheduler instance
    properties.setProperty("org.quartz.scheduler.instanceName", "QuartzScheduler");
    // Đặt ID duy nhất cho scheduler instance
    properties.setProperty("org.quartz.scheduler.instanceId", "Instance1");

    // Cho phép ghi đè các job đã tồn tại khi restart
    schedulerFactoryBean.setOverwriteExistingJobs(true);
    // Tự động start scheduler khi ứng dụng khởi động
    schedulerFactoryBean.setAutoStartup(true);
    // Set các properties đã cấu hình cho scheduler
    schedulerFactoryBean.setQuartzProperties(properties);
    // Set job factory có khả năng autowire
    schedulerFactoryBean.setJobFactory(springBeanJobFactory());
    // Đợi các job hoàn thành trước khi shutdown ứng dụng
    schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

    // Lấy danh sách các job được phép chạy từ config
    Set<String> runningJobs = quartzRunningJobConfig.getRunningJobs();
    // Log danh sách job được phép chạy
    log.info("RUNNING JOBS: {}", CommonUtils.toJson(runningJobs));

    // Kiểm tra xem có trigger nào được truyền vào không
    if (triggers != null && triggers.length > 0) {
      // Lọc ra những trigger có job name nằm trong danh sách được phép chạy
      List<Trigger> triggerFiltered = Arrays.stream(triggers)
          .filter(trigger -> runningJobs.contains(trigger.getJobKey().getName()))
          .toList();

      // Tạo mảng trigger từ list ��ã lọc
      Trigger[] triggerFilterArray = new Trigger[triggerFiltered.size()];
      // Copy từng trigger vào mảng và log thông tin
      for (int i = 0; i < triggerFiltered.size(); i++) {
        triggerFilterArray[i] = triggerFiltered.get(i);
        // Log tên job sẽ được chạy
        log.info("PREPARE TO RUN: [{}]", triggerFilterArray[i].getJobKey().getName());
      }
      // Set mảng trigger đã lọc cho scheduler
      schedulerFactoryBean.setTriggers(triggerFilterArray);
    }
    return schedulerFactoryBean;
  }

  // Phương thức static để tạo JobDetailFactoryBean
  public static JobDetailFactoryBean createJobDetail(Class<? extends Job> jobClass, String jobName) {
    // Log thông tin tạo job detail
    log.info("createJobDetail(jobClass: {}, jobName: {})", jobClass.getName(), jobName);
    // Tạo JobDetailFactoryBean
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    // Set tên cho job
    factoryBean.setName(jobName);
    // Set class của job
    factoryBean.setJobClass(jobClass);
    // Set durability = true để job không bị xóa khi không có trigger
    factoryBean.setDurability(true);
    return factoryBean;
  }

  // Phương thức static để tạo CronTriggerFactoryBean (trigger chạy theo cron expression)
  public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression, String triggerName) {
    // Log thông tin tạo cron trigger
    log.info("createCronTrigger(jobDetail: {}, cronExpression: {}, triggerName: {})", jobDetail.getKey(), cronExpression, triggerName);

    // Tạo Calendar để set thời gian bắt đầu
    Calendar calendar = Calendar.getInstance();
    // Set giây về 0 để trigger chạy đúng phút
    calendar.set(Calendar.SECOND, 0);
    // Set millisecond về 0 để chính xác hơn
    calendar.set(Calendar.MILLISECOND, 0);

    // Tạo CronTriggerFactoryBean
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    // Set job detail cho trigger
    factoryBean.setJobDetail(jobDetail);
    // Set cron expression (định nghĩa lịch chạy)
    factoryBean.setCronExpression(cronExpression);
    // Set thời gian bắt đầu
    factoryBean.setStartTime(calendar.getTime());
    // Set delay = 0 để không có độ trễ
    factoryBean.setStartDelay(0L);
    // Set tên cho trigger
    factoryBean.setName(triggerName);
    // Set misfire instruction: không làm gì khi miss fire
    factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);

    return factoryBean;
  }

  // Phương thức static để tạo SimpleTriggerFactoryBean (trigger chạy theo interval)
  public static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFreqMs, String triggerName) {
    // Log thông tin tạo simple trigger
    log.info("createTrigger(jobDetail: {}, pollFreqMs: {}, triggerName: {})", jobDetail.getKey(), pollFreqMs, triggerName);
    // Tạo SimpleTriggerFactoryBean
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    // Set job detail cho trigger
    factoryBean.setJobDetail(jobDetail);
    // Set delay = 0 để trigger chạy ngay
    factoryBean.setStartDelay(0L);
    // Set interval giữa các lần chạy (tính bằng milliseconds)
    factoryBean.setRepeatInterval(pollFreqMs);
    // Set tên cho trigger
    factoryBean.setName(triggerName);
    // Set repeat count = vô hạn để trigger chạy mãi mãi
    factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    // Set misfire instruction: reschedule với remaining count khi miss fire
    factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
    return factoryBean;
  }
}
