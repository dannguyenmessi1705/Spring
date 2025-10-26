package com.didan.logquickwit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class LogQuickwitApplication {

  public static void main(String[] args) {
    SpringApplication.run(LogQuickwitApplication.class, args);
  }

}
