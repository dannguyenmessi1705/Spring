package com.didan.reactive.redissonstartup;

import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = RedissonAutoConfigurationV2.class)
@EnableCaching
public class RedissonStartupApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedissonStartupApplication.class, args);
  }

}
