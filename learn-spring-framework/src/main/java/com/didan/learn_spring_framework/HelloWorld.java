package com.didan.learn_spring_framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuarion là một annotation dùng để đánh dấu một class là một configuration class, nó sẽ được Spring container sử dụng để cấu hình và quản lý các bean.
@Configuration
public class HelloWorld {
	
	// @Bean là một annotation dùng để đánh dấu một method trả về một bean, Spring container sẽ quản lý và cung cấp bean này.
	@Bean
	public String name() {
		return "Dan";
	}
}
