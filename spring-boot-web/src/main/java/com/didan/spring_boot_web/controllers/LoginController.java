package com.didan.spring_boot_web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
	@RequestMapping("/login")
	public String loginPage(@RequestParam("name") String name, ModelMap model) {
		if (StringUtils.hasText(name)) {
			model.put("name", name);
		}
		return "login";
	}

}
