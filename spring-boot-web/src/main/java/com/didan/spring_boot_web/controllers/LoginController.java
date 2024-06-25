package com.didan.spring_boot_web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
	private final Logger logger = LoggerFactory.getLogger(getClass()); // Sử dụng Logger để ghi log, lấy thông tin
																		// của class hiện tại

	@RequestMapping("/login")
	public String loginPage(@RequestParam("name") String name, ModelMap model) {
		if (StringUtils.hasText(name)) {
			model.put("name", name); // Truyền dữ liệu từ Controller sang View thông qua ModelMap, với key là "name"
										// và value là name, sau đó file JSP có thể sử dụng được dữ liệu này bằng cú
										// pháp ${name}
		}
		logger.debug("Query is {}", name); // Ghi log với level DEBUG, in ra giá trị của name, DEGUB: Query is <name>
		return "login";
	}

}
