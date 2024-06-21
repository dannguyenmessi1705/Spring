package com.didan.learn_spring_framework.game;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary // Annotation đánh dấu đây là Bean mặc định khi có nhiều Bean cùng loại
public class MarioGame implements GameConsole{
	public void up () {
		System.out.println("Jump");
	}
	
	public void down () {
		System.out.println("Go into hole");
	}
	
	public void left () {
		System.out.println("Go back");
	}
	
	public void right () {
		System.out.println("Accelerate");
	}
}
