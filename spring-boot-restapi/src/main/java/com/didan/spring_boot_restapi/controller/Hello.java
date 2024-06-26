package com.didan.spring_boot_restapi.controller;

public class Hello {
	private String message;
	
	public Hello(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
