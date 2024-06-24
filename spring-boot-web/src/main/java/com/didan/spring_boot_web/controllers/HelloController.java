package com.didan.spring_boot_web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // Đánh dấu đây là một Controller của Spring MVC (không phải RestController chỉ
			// trả về dữ liệu JSON hoặc XML)
public class HelloController {
	@RequestMapping("/hello") // Đánh dấu phương thức này sẽ xử lý request từ client với URL là /hello
	@ResponseBody // Phương thức này sẽ trả về dữ liệu trực tiếp cho client (không cần thông qua
					// View)
	public String hello() {
		// Trả về một chuỗi HTML thì phải nối chuỗi bằng StringBuilder
		StringBuilder html = new StringBuilder();
		html.append("<html>")
				.append("<title>")
				.append("My Spring MVC")
				.append("</title>")
				.append("<body>")
				.append("My Spring MVC")
				.append("</body")
				.append("</html>");

		return html.toString(); // Trả về chuỗi HTML cho client
	}

	@RequestMapping("/hello-jsp") // Đánh dấu phương thức này sẽ xử lý request từ client với URL là /hello-jsp
	public String helloJsp() {
		return "helloWorld"; // Trả về tên của file JSP mà bạn muốn hiển thị (= spring.mvc.view.prefix +
								// "helloWorld" + spring.mvc.view.suffix = /WEB-INF/views/helloWorld.jsp)
	}
}
