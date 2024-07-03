package com.didan.learn_spring_security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JWTSecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// Authorize Requests
		http.authorizeHttpRequests(req -> req.anyRequest().authenticated())
		.httpBasic(withDefaults())
		.cors(cors -> cors.disable())
		.csrf(csrf -> csrf.disable())
		.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
		.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())); // Sử dụng JWT
		
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
	
	/// JWT
	// 1. Tạo Key Pair
	@Bean
	public KeyPair keyPair() throws Exception{ // Tạo một KeyPair bao gồm public key và private key
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // Sử dụng thuật toán RSA
		keyPairGenerator.initialize(2048); // Khởi tạo key với độ dài 2048 bits
		return keyPairGenerator.generateKeyPair(); // Trả về một KeyPair
	}
	
	// 2. Taọ đối tượng khóa RSA sử dụng KeyPair
	@Bean
	public RSAKey rsaKey(KeyPair keyPair) { // Tạo một RSAKey từ KeyPair
		return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()) // Sử dụng public key
				.privateKey(keyPair.getPrivate()) // Sử dụng private key
				.keyID(UUID.randomUUID().toString()) // Tạo một keyID ngẫu nhiên
				.build(); // Tạo một RSAKey
	}
	
	// 3. Tạo JWKSource
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) { // Tạo một JWKSource từ RSAKey
		JWKSet jwkSet = new JWKSet(rsaKey); // Tạo một JWKSet từ RSAKey
		return (jwkSelector, context) -> jwkSelector.select(jwkSet); // Trả về một JWKSource
	}
	
	// 4. Tạo phương thức giải mã token JWT từ RSA public key
	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException { // Tạo một JwtDecoder từ RSAKey để giải mã token JWT
		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build(); // Trả về một JwtDecoder
	}
	
	// 5. Tạo phương thức mã hóa JWKSource (RSAKey)
	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource); // Trả về một JwtEncoder từ JWKSource, sử dụng NimbusJwtEncoder
	}
}
