package com.didan.schedule.config;

import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "quartz.jobs")
public class QuartzRunningJobConfig {
  private Set<String> runningJobs;
}
