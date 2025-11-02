package com.didan.reactive.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.didan.reactive.${package}")
public class R2dbcApplication {

  public static void main(String[] args) {
    SpringApplication.run(R2dbcApplication.class, args);
  }

}
