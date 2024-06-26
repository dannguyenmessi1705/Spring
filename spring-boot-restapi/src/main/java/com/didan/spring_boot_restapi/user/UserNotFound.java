package com.didan.spring_boot_restapi.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFound extends RuntimeException {
	public UserNotFound(String message) {
		super(message);
	}
}
