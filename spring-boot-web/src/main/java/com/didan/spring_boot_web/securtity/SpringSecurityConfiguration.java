package com.didan.spring_boot_web.securtity;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration // Đánh dấu đây là class cấu hình Spring (Bên trong có thể chứa các Bean)
public class SpringSecurityConfiguration {

	@Bean // Bean này sẽ được Spring quản lý
	public PasswordEncoder passwordEncoder() { // Bean này trả về một đối tượng PasswordEncoder, dùng để mã hóa mật khẩu
		return new BCryptPasswordEncoder(); // Trả về một đối tượng BCryptPasswordEncoder
	}

	// Trong Spring Security, có các cách xác thực người dùng như: InMemory,
	// Database, LDAP, ...

	@Bean // Bean này sẽ được Spring quản lý
	public InMemoryUserDetailsManager createUserDetailsManager() { // Bean này trả về một đối tượng
																	// InMemoryUserDetailsManager, dùng để tạo ra một
																	// user trong bộ nhớ
		Function<String, String> passwordEncoderFunction = input -> passwordEncoder().encode(input); // Tạo ra một
																										// đối tượng
																										// Function
																										// để mã hóa
																										// mật khẩu

		UserDetails userDetails1 = User.builder().passwordEncoder(passwordEncoderFunction).username("didannguyen")
				.password("12345").roles("ADMIN", "USER").build(); // Tạo ra một đối tượng UserDetails với việc được mã
																	// hóa mật khẩu, username là didannguyen, 
																	// password là 12345, và có 2 role là
																	// ADMIN và USER
		
		UserDetails userDetails2 = User.builder().passwordEncoder(passwordEncoderFunction).username("lionelmessi")
				.password("12345").roles("ADMIN", "USER").build(); // Tạo ra một đối tượng UserDetails với việc được mã
																	// hóa mật khẩu, username là didannguyen, 
																	// password là 12345, và có 2 role là
																	// ADMIN và USER
		return new InMemoryUserDetailsManager(userDetails1, userDetails2); // Trả về một đối tượng InMemoryUserDetailsManager với
															// user vừa tạo
	}
}
