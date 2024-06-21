package com.didan.learn_spring_framework.game;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("SuperContraGame") // Annotation đánh dấu đây là Bean với tên là "SuperContraGame" để sử dụng
								// @Qualifier chọn Bean cụ thể cần inject
public class SuperContraGame implements GameConsole {
	public void up() {
		System.out.println("Up");
	}

	public void down() {
		System.out.println("Sit down");
	}

	public void left() {
		System.out.println("Go back");
	}

	public void right() {
		System.out.println("Shoot a bundle");
	}
}
