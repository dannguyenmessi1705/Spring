package com.didan.learn_spring_framework;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App02GamingBasicJava {

	public static void main(String[] args) {
		// Tạo một Spring container bằng cách cung cấp một configuration class chứa các
		// cấu hình về các bean.
		// AnnotationConfigApplicationContext là một implementation của interface
		// ApplicationContext, nó sử dụng các thông tin được cung cấp từ các annotation
		// để cấu hình và quản lý các bean.
		var app = new AnnotationConfigApplicationContext(HelloWorld.class);

		// Lấy bean từ Spring container.
		// getName() trả về tên của bean, nếu không được chỉ định thì nó sẽ là tên của
		// method trả về bean.
		System.out.println(app.getBean("name"));

	}

}
