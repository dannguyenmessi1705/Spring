package com.didan.learn_spring_framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

record Person(String name, int age) {};

record Address(String firstLine, String city) {};

// @Configuarion là một annotation dùng để đánh dấu một class là một configuration class, nó sẽ được Spring container sử dụng để cấu hình và quản lý các bean.
@Configuration
public class HelloWorld {
	
	// @Bean là một annotation dùng để đánh dấu một method trả về một bean, Spring container sẽ quản lý và cung cấp bean này.
	@Bean
	public String name() {
		return "Dan";
	}
	
	@Bean
	public Person person() {
		return new Person("Zidane", 54);
	}
	
	@Bean Address address() {
		return new Address("Nguyen Trai", "Ha Noi");
	}
}
