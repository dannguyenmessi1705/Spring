package com.didan.spring_boot_restapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@GetMapping(path = "/hello")
	public Hello hello() {
		return new Hello("Hello World!");
	}
	
	@GetMapping(path = "/hello2/{id}")
	public Hello hello2(@RequestParam("name") String name, @PathVariable("id") int id) {
		return new Hello("Hello " + name + " with id " + id);
	}

}
