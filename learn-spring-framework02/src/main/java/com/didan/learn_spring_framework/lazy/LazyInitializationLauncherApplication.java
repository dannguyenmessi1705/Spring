package com.didan.learn_spring_framework.lazy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.didan.learn_spring_framework.game.GameConsole;
import com.didan.learn_spring_framework.game.GameRunner;

@Component
@Lazy // Annotation này làm cho Bean chỉ được khởi tạo khi nó được gọi đến
class ClassA {

}

@Component
@Lazy // Annotation này làm cho Bean chỉ được khởi tạo khi nó được gọi đến
class ClassB {
	// DI
	private ClassA classA;

	public ClassB(ClassA classA) {
		System.out.println("Initial logic");
		this.classA = classA;
	}

	public void display() {
		System.out.println("Do something");
	}

}

@Configuration
@ComponentScan()
public class LazyInitializationLauncherApplication {

	public static void main(String[] args) {
		// Dùng try-with-resources để tự đóng context sau khi sử dụng xong
		try (var context = new AnnotationConfigApplicationContext(LazyInitializationLauncherApplication.class)) {
			// Nếu không dùng @Lazy thì cả 2 Bean sẽ được khởi tạo ngay khi context được
			// khởi tạo, in ra trước "Initialization successfully"
			System.out.println("Initialization successfully");
			// Khi gọi đến Bean ClassB thì Bean ClassA mới được khởi tạo
			context.getBean(ClassB.class).display(); // Vì @Lazy nên Bean ClassA sẽ được khởi tạo sau khi Bean ClassB
														// được gọi đến, in ra sau "Do something"
		}

	}

}
