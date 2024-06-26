package com.didan.spring_boot_web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.didan.spring_boot_web.services.LoginService;

@Controller
@SessionAttributes("username") // Lưu trữ thông tin username vào Session để sử dụng ở các request khác, khi có
								// ModelMap put vào key "username"
public class LoginController {
//	private final Logger logger = LoggerFactory.getLogger(getClass()); // Sử dụng Logger để ghi log, lấy thông tin
//																		// của class hiện tại
//	private final LoginService loginService; // Inject một Bean vào Controller từ Service
//
//	public LoginController(LoginService loginService) {
//		super();
//		this.loginService = loginService;
//	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET) // Xử lý request GET từ client với URL là /
	public String homePage(ModelMap model) {
		model.put("username", getUsernameInSecurity()); // Lưu thông tin username vào model render View  
		return "welcome";
		
	}
	
//	@RequestMapping(value = "login", method = RequestMethod.GET) // Xử lý request GET từ client với URL là /login
//	public String loginPage() {
//		return "login";
//	}
//
//	@RequestMapping(value = "login", method = RequestMethod.POST) // Xử lý request POST từ client với URL là /login
//	public String postLogin(@RequestParam("username") String username, @RequestParam("password") String password,
//			ModelMap model) { // RequestParam để lấy dữ liệu từ form gửi lên, hoặc từ query parameter
//		// ModelMap để truyền dữ liệu từ Controller sang View, từ View: hiển thị dữ liệu
//		// bằng ${key} (key là tên của biến truyền từ Controller)
//		if (loginService.checkLogin(username, password)) {
//			model.put("username", username); // Lưu thông tin vào model render View, đồng thời lưu username vào Session
//												// vì đã có @SesionAttributes("username") ở trên
//			return "welcome";
//		} else {
//			model.put("authError", "Incorrect username or password");
//			return "login";
//		}
//	}
	
	private String getUsernameInSecurity() { // Lấy thông tin username từ SecurityContextHolder sau khi đăng nhập
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Lấy thông tin
																								// Authentication từ SecurityContextHolder
		return authentication.getName(); // Lấy thông tin username từ Authentication
	}

}
