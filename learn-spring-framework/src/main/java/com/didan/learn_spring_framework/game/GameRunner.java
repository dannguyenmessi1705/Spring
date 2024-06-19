package com.didan.learn_spring_framework.game;

public class GameRunner {
	private GameConsole game;
	public GameRunner(GameConsole game) {
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
