package com.didan.learn_spring_security;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl; // Import để sử dụng script tạo bảng user
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// Authorize Requests
		http.authorizeHttpRequests(req -> req.anyRequest().authenticated())
		.httpBasic(withDefaults())
		.cors(cors -> cors.disable())
		.csrf(csrf -> csrf.disable())
		.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
		
		return http.build();
	}

	@Bean
	public DataSource dataSource() { // Bean này sẽ tạo ra một database H2, tạo bảng user và insert dữ liệu vào bảng user
		return new EmbeddedDatabaseBuilder() // Tạo một database H2
				.setType(EmbeddedDatabaseType.H2) // Loại database H2
				.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION) // Tạo bảng user
				.build(); // Tạo database
	}
	
	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) { // Bean này sẽ insert dữ liệu vào bảng user
		UserDetails user1 = User.withUsername("didan")
				.password("{noop}didan") // password: didan, {noop} is a password encoder that does nothing
				.roles("ADMIN", "USER")
				.build();
		UserDetails user2 = User.withUsername("user")
				.password("{noop}user") // password: user, {noop} is a password encoder that does nothing
				.roles("USER").build();
		UserDetails user3 = User.withUsername("admin")
				.password("{noop}admin") // password: admin, {noop} is a password encoder that does nothing
				.roles("ADMIN").build();
		
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource); // Tạo một JdbcUserDetailsManager
		jdbcUserDetailsManager.createUser(user1); // Insert dữ liệu vào bảng user
		jdbcUserDetailsManager.createUser(user2); // Insert dữ liệu vào bảng user
		jdbcUserDetailsManager.createUser(user3); // Insert dữ liệu vào bảng user
		return jdbcUserDetailsManager;
	}
}
