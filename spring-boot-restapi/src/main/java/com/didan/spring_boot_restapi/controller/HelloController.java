package com.didan.spring_boot_restapi.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	private MessageSource messageSource; // Inject MessageSource để có thể đọc message từ file messages.properties
	public HelloController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@GetMapping(path = {"/hello", "/dan"}) // Tạo path cho method này là /hello hoặc /dan
	public Hello hello() {
		return new Hello("Hello World!");
	}
	
	@GetMapping(path = "/hello2/{id}")
	public Hello hello2(@RequestParam("name") String name, @PathVariable("id") int id) {
		return new Hello("Hello " + name + " with id " + id);
	}
	
	@GetMapping(path = "/hello-international/{name}")
	public ResponseEntity<? super String> helloInternational(@PathVariable("name") String name) {
		String[] arr = new String[] {name, LocalDate.now().toString()}; // Tạo một mảng chứa các tham số truyền vào message, tham chiếu trong messsages.properties sẽ lần lượt là {0}, {1}, ...
		Locale locale = LocaleContextHolder.getLocale(); // Lấy locale từ LocaleContextHolder, từ header của request có key là Accept-Language
		String message = messageSource.getMessage("good.morning.message", arr, "Default message", locale); // Đọc message từ file messages.properties, null là các tham số truyền vào message, "Default message" là message mặc định nếu không tìm thấy message trong file messages.properties, locale là locale lấy từ LocaleContextHolder
		Map<String, String> map = new HashMap<>(); // Tạo một map để chứa message, do muốn trả về message dưới dạng JSON nên tạo map, vì ResponseEntity không thể trả về dữ liệu mà không có key
		map.put("message", message); // Thêm message vào map
		return new ResponseEntity<>(map, HttpStatus.OK); // Trả về message trong map
	}

}
