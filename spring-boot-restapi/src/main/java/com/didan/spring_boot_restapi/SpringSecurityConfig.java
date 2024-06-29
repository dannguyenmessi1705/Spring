package com.didan.spring_boot_restapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static  org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SpringSecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// Xác thực tất cả các request
		http.authorizeHttpRequests(req -> req.anyRequest().authenticated());
		
		// Sử dụng form login nếu không xác thực được
		http.httpBasic(withDefaults());
		
		// Tắt CSRF cho POST, PUT, DELETE request
		http.csrf(csrf -> csrf.disable());
		
		return http.build();
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // Cho phép tất cả đường dẫn
				.allowedMethods("*") // Cho phép tất cả các phương thức
				.allowedOrigins("*"); // Cho phép tất cả các domain khác gọi API
			}
		};
	}

}
