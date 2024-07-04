package com.didan.learn_spring_aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
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
	
	@AfterThrowing(pointcut = "execution(* com.didan.learn_spring_aop.service.*.*(..))", 
			throwing = "ex") // // Tạo Pointcut cho method trong package service khi method ném ra 1 ngoại lệ
	public void afterThrowing(JoinPoint joinPoint, Exception ex) { // Annotation AfterThrowing sẽ chạy khi method gặp lỗi
		logger.error("Call AfterThrowing - Method is called {} with exception {}", joinPoint, ex);
	}
	
	@AfterReturning(pointcut = "execution(* com.didan.learn_spring_aop.service.*.*(..))", 
            returning = "result") // Tạo Pointcut cho method trong package service khi method trả về kết quả
	public void afterReturning(JoinPoint joinPoint, Object result) { // Annotation AfterReturning sẽ chạy khi method trả về kết quả
		logger.info("Call AfterReturning - Method is called {} with result {}", joinPoint, result);
	}
	
	@Around("execution(* com.didan.learn_spring_aop.service.*.*(..))") // Tạo Pointcut cho method trong package service)
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		// Trước khi chạy method
		long startTime = System.currentTimeMillis(); // Lấy thời gian hiện tại
		
		// Chạy method
		Object result = proceedingJoinPoint.proceed(); // Chạy method được đánh dấu bởi Pointcut
		
		// Sau khi chạy method
		long endTime = System.currentTimeMillis(); // Lấy thời gian sau khi chạy method
		
		logger.info("Call Around - Method is called {} with time {}ms", proceedingJoinPoint, endTime - startTime);
		
		return result; // Trả về kết quả của method
		
	}
}
