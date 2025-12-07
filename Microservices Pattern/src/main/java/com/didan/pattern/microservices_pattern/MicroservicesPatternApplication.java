package com.didan.pattern.microservices_pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.didan.pattern.microservices_pattern.orchestrator_parallel")
public class MicroservicesPatternApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicesPatternApplication.class, args);
	}

}
