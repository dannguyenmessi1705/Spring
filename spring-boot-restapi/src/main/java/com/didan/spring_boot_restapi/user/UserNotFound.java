package com.didan.spring_boot_restapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND) // Tạo ra status code 404 
public class UserNotFound extends RuntimeException { // Kế thừa từ RuntimeException
	public UserNotFound(String message) {
		super(message); // Gọi constructor của lớp cha
	}
}
