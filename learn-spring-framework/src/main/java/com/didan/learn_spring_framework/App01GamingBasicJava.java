package com.didan.learn_spring_framework;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.didan.learn_spring_framework.game.GameConsole;
import com.didan.learn_spring_framework.game.GameRunner;
import com.didan.learn_spring_framework.game.MarioGame;
import com.didan.learn_spring_framework.game.PacmanGame;
import com.didan.learn_spring_framework.game.SuperContraGame;

public class App01GamingBasicJava {

	public static void main(String[] args) {
		// var game = new MarioGame();
		// var game = new SuperContraGame();
		// var game = new PacmanGame();
		// var gameRunner = new GameRunner(game);

		// Dùng try-with-resources để tự đóng context sau khi sử dụng xong
		try (var context = new AnnotationConfigApplicationContext(GameConfiguration.class)) {
			context.getBean(GameConsole.class).up(); // up() là một phương thức của interface GameConsole mà các game
														// class implement
			context.getBean(GameRunner.class).run(); // run() là một phương thức của class GameRunner mà chúng ta đã tạo
		}

	}

}
