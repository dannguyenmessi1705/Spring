package com.didan.learn_spring_framework.scope;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.didan.learn_spring_framework.game.GameConsole;
import com.didan.learn_spring_framework.game.GameRunner;

@Component
class NormalClass {

}

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Bean sẽ được tạo mới mỗi lần được gọi đến
// Nếu không có @Scope thì Bean sẽ được tạo một lần duy nhất khi context được
// khởi tạo
// ConfigurableBeanFactory.SCOPE_PROTOTYPE: Bean sẽ được tạo mới mỗi lần được
// gọi đến
// ConfigurableBeanFactory.SCOPE_SINGLETON: Bean sẽ chỉ được tạo một lần duy
// nhất khi context được khởi tạo
class PrototypeClass {

}

@Configuration
@ComponentScan()
public class ScopeLauncherApplication {

	public static void main(String[] args) {
		// Dùng try-with-resources để tự đóng context sau khi sử dụng xong
		try (var context = new AnnotationConfigApplicationContext(ScopeLauncherApplication.class)) {
			// Mặc định sẽ là Singleton, in ra cùng một địa chỉ vùng nhớ
			System.out.println(context.getBean(NormalClass.class));
			System.out.println(context.getBean(NormalClass.class));
			System.out.println(context.getBean(NormalClass.class));

			// Prototype, in ra các địa chỉ vùng nhớ khác nhau mỗi lần được gọi đến
			System.out.println(context.getBean(PrototypeClass.class));
			System.out.println(context.getBean(PrototypeClass.class));
			System.out.println(context.getBean(PrototypeClass.class));
		}

	}

}
