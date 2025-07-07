package com.didan.elastic;

import com.didan.archetype.annotation.EnableArchetype;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableArchetype
public class ElasticApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElasticApplication.class, args);
  }

}
