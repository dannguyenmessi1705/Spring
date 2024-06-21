package com.didan.learn_spring_framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.didan.learn_spring_framework.game.GameConsole;
import com.didan.learn_spring_framework.game.GameRunner;
import com.didan.learn_spring_framework.game.PacmanGame;

@Configuration
public class GameConfiguration {
	@Bean
	public GameConsole game() {
		var game = new PacmanGame();
		return game;
	}
	
	@Bean GameRunner gameRunner(GameConsole game) {
		var gameRunner = new GameRunner(game);
		return gameRunner;
	}
}
