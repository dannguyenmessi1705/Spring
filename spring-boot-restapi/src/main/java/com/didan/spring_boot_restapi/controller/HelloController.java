package com.didan.spring_boot_restapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@GetMapping(path = "/hello")
	public Hello hello() {
		return new Hello("Hello World!");
	}

}
