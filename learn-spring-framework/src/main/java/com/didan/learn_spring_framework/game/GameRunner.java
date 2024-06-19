package com.didan.learn_spring_framework.game;

public class GameRunner {
//	private MarioGame marioGame;
	private SuperContraGame superContraGame;
	public GameRunner(SuperContraGame superContraGame) {
		this.superContraGame = superContraGame;
	}
	
	public void run() {
		System.out.println("Game Running " + superContraGame);
		
		superContraGame.up();
		superContraGame.down();
		superContraGame.left();
		superContraGame.right();
	}

}
