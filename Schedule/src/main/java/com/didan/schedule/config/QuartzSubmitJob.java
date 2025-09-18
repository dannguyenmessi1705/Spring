package com.didan.schedule.config;

// Import class UseQuartz - đây là job implementation sẽ được thực thi
import com.didan.schedule.processor.UseQuartz;
// Import JobDetail từ Quartz để định nghĩa job
import org.quartz.JobDetail;
// Import Qualifier để chỉ định bean cụ thể khi inject
import org.springframework.beans.factory.annotation.Qualifier;
// Import Bean annotation để đánh dấu phương thức tạo bean
import org.springframework.context.annotation.Bean;
// Import Configuration để đánh dấu class này là configuration class
import org.springframework.context.annotation.Configuration;
// Import các factory bean để tạo cron trigger
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
// Import factory bean để tạo job detail
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
// Import factory bean để tạo simple trigger
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// Annotation đánh dấu đây là class cấu hình Spring
@Configuration
public class QuartzSubmitJob {

  // Bean tạo JobDetail cho User Service
  // name = "jobUserService" - đặt tên cho bean để có thể reference sau này
  @Bean(name = "jobUserService")
  public JobDetailFactoryBean jobUserService() {
    // Sử dụng utility method từ QuartzConfig để tạo JobDetail
    // UseQuartz.class - class chứa logic xử lý job
    // "User Service" - tên hiển thị của job
    return QuartzConfig.createJobDetail(UseQuartz.class, "User Service");
  }

  // Bean tạo SimpleTrigger cho User Service - chạy theo interval cố định
  // name = "triggerJobUserService" - đặt tên cho trigger bean
  @Bean(name = "triggerJobUserService")
  public SimpleTriggerFactoryBean triggerJobUserService(@Qualifier("jobUserService")JobDetail jobDetail) {
    // @Qualifier("jobUserService") - chỉ định inject bean jobUserService đã tạo ở trên
    // JobDetail jobDetail - job detail sẽ được gắn với trigger này

    // Sử dụng utility method từ QuartzConfig để tạo SimpleTrigger
    // jobDetail - job sẽ được trigger thực thi
    // 5000 - interval 5000ms (5 giây) giữa các lần chạy
    // "triggerUserService" - tên của trigger
    return QuartzConfig.createTrigger(jobDetail, 5000, "triggerUserService");
  }

  // Bean tạo CronTrigger cho User Service - chạy theo cron expression
  // name = "cronTriggerJobUserService" - đặt tên cho cron trigger bean
  @Bean(name = "cronTriggerJobUserService")
  public CronTriggerFactoryBean cronTriggerJobUserService(@Qualifier("jobUserService")JobDetail jobDetail) {
    // @Qualifier("jobUserService") - chỉ định inject bean jobUserService đã tạo ở trên
    // JobDetail jobDetail - job detail sẽ được gắn với cron trigger này

    // Sử dụng utility method từ QuartzConfig để tạo CronTrigger
    // jobDetail - job sẽ được trigger thực thi
    // "0/5 * * * * ?" - cron expression: chạy mỗi 5 giây
    //   0/5 - bắt đầu từ giây 0, lặp lại mỗi 5 giây
    //   * - mọi phút
    //   * - mọi giờ
    //   * - mọi ngày trong tháng
    //   * - mọi tháng
    //   ? - không quan tâm ngày trong tuần
    // "cronTriggerUserService" - tên của cron trigger
    return QuartzConfig.createCronTrigger(jobDetail, "0/5 * * * * ?", "cronTriggerUserService");
  }
}
