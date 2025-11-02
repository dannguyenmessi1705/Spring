package com.didan.reactive.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "com.didan.reactive.learn.${package}")
@EnableR2dbcRepositories(basePackages = "com.didan.reactive.learn.${package}")
public class ReactiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReactiveApplication.class, args);
  }

}
