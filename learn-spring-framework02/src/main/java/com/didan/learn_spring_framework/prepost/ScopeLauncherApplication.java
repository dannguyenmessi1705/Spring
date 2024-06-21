package com.didan.learn_spring_framework.prepost;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
class ClassA {
	public void display() {
		System.out.println("Display classA");
	}
}

@Component
class ClassB {
	// DI
	private final ClassA classA;

	public ClassB(ClassA classA) {
		super();
		this.classA = classA;
		System.out.println("All dependencies initiated");
	}

	@PostConstruct // Bean sẽ được khởi tạo, và được thực thi sau khi tất cả các dependency được
					// khởi tạo
	public void init() {
		classA.display();
	}

	@PreDestroy // Bean sẽ được hủy trước khi context bị đóng
	public void destruct() {
		System.out.println("Bye bye");
	}
}

@Configuration
@ComponentScan()
public class ScopeLauncherApplication {

	public static void main(String[] args) {
		// Dùng try-with-resources để tự đóng context sau khi sử dụng xong
		try (var context = new AnnotationConfigApplicationContext(ScopeLauncherApplication.class)) {
			Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
			// Sau khi context được khởi tạo, Bean ClassB sẽ được khởi tạo, in ra "All dependencies initiated"
			// Sau đó sẽ load bean init() của ClassB, in ra "Display classA" trước khi thực hiện các logic khác
			// Khi context bị đóng, Bean ClassB sẽ được hủy, in ra "Bye bye"
		}

	}

}
