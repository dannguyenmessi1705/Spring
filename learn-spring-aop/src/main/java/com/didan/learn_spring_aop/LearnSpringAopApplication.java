package com.didan.learn_spring_aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.didan.learn_spring_aop.service.Business;

@SpringBootApplication
public class LearnSpringAopApplication implements CommandLineRunner{
	
	private final Business business;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public LearnSpringAopApplication(Business business) {
		this.business = business;
	}

	public static void main(String[] args) {
		SpringApplication.run(LearnSpringAopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("The max of arrays is {}", business.getMax());
		
	}

}
