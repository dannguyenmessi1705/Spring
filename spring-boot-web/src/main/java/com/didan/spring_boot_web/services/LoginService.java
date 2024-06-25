package com.didan.spring_boot_web.services;

import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là một Service của Spring dùng để xử lý logic
public class LoginService {
	public boolean checkLogin(String username, String password) { // Kiểm tra thông tin đăng nhập
		boolean checkuser = username.equalsIgnoreCase("didannguyen"); // Kiểm tra username
		boolean checkpass = password.equalsIgnoreCase("12345"); // Kiểm tra password
		return checkuser && checkpass; // Trả về true nếu cả hai điều kiện đều đúng
	}
}
