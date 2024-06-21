package com.didan.learn_spring_framework.game;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component // Annotaion đánh dấu đây là một Bean
public class GameRunner {
	private GameConsole game;
	public GameRunner(@Qualifier("SuperContraGame") GameConsole game) { // Sử dụng @Qualifier để chọn Bean cụ thể cần inject
		this.game = game;
	}
	
	public void run() {
		System.out.println("Game Running " + game);
		
		game.up();
		game.down();
		game.left();
		game.right();
	}

}
