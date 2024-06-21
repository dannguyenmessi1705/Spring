package com.didan.learn_spring_framework;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.didan.learn_spring_framework.game.GameConsole;
import com.didan.learn_spring_framework.game.GameRunner;

@Configuration
@ComponentScan("com.didan.learn_spring_framework.game")
public class GamingAppLauncherApplication {

	public static void main(String[] args) {
		// Dùng try-with-resources để tự đóng context sau khi sử dụng xong
		try (var context = new AnnotationConfigApplicationContext(GamingAppLauncherApplication.class)) {
			context.getBean(GameConsole.class).up(); // up() là một phương thức của interface GameConsole mà các game
														// class implement
			context.getBean(GameRunner.class).run(); // run() là một phương thức của class GameRunner mà chúng ta đã tạo
		}

	}

}
