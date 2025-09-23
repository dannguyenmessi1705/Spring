package com.didan.schedule.processor;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public abstract class QuartzContextJob implements Job {

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    run(jobExecutionContext);
  }

  public abstract void run(JobExecutionContext context);
}
