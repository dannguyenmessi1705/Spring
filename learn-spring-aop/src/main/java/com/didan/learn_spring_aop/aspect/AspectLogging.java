package com.didan.learn_spring_aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class AspectLogging {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Before("execution(* com.didan.learn_spring_aop.service.*.*(..))") // Tạo Pointcut cho method trong package service với bất kỳ tham số nào và trả về bất kỳ kiểu dữ liệu nào
	public void beforeRun(JoinPoint joinPoint) { // Annotation Before sẽ chạy trước khi method được gọi
		// JoinPoint là một interface trong AspectJ, nó cung cấp thông tin về method được gọi
		logger.info("Call Before - Method is called {}", joinPoint);
	}
}
