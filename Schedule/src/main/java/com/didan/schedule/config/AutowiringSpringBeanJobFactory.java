package com.didan.schedule.config;

// Import các class cần thiết từ Quartz và Spring
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

// Lớp này mở rộng SpringBeanJobFactory để hỗ trợ tự động autowire các bean trong job của Quartz
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

  // Biến lưu trữ beanFactory để autowire các bean
  private AutowireCapableBeanFactory beanFactory;

  // Phương thức này được gọi bởi Spring để truyền ApplicationContext vào class này
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    // Lấy ra AutowireCapableBeanFactory từ ApplicationContext để sử dụng autowire
    beanFactory = applicationContext.getAutowireCapableBeanFactory();
  }

  // Ghi đè phương thức tạo instance của job, đồng thời autowire các dependency cho job
  @Override
  protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
    // Tạo instance của job thông qua SpringBeanJobFactory
    Object job = super.createJobInstance(bundle);
    // Thực hiện autowire các dependency cho job vừa tạo
    beanFactory.autowireBean(job);
    // Trả về job đã được autowire
    return job;
  }
}
