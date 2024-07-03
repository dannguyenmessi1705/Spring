package com.didan.learn_spring_security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtAuthenticationController {
	private final JwtEncoder jwtEncoder; // Inject JwtEncoder (từ class JWTSecurityConfig) vào JwtAuthenticationController để mã hóa token JWT
	
	public JwtAuthenticationController(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}
	
	@PostMapping("/authenticate") // Phương thức để xác thực người dùng, lấy token sau khi xác thực
	public JwtResponse authenticate(Authentication authentication) { // Authentication là một interface, nó chứa thông tin về người dùng sau khi xác thực Basic Authentication
		return new JwtResponse(createToken(authentication)); // Trả về token sau khi xác thực
	}
	
	
	private String createToken(Authentication authentication) { // Tạo token JWT từ thông tin người dùng sau khi xác thực
		JwtClaimsSet claims = JwtClaimsSet.builder() // Tạo claims cho token JWT
				.issuer("self") // Issuer của token là chính nó
				.issuedAt(Instant.now()) // Thời gian phát hành token
				.expiresAt(Instant.now().plusSeconds(60 * 15)) // Token hết hạn sau 15 phút
				.subject(authentication.getName()) // Subject của token là username của người dùng (có thể lấy bằng cáhc autthentication.getPricipal());
				.claim("scope", createScope(authentication)) // Tạo thêm trường tùy chỉnh trong body payload của token JWT, ở đây là scope
				.build(); // Tạo claims
		
		JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(claims); // Tạo JwtEncoderParameters từ claims vừa tạo
		return jwtEncoder.encode(jwtEncoderParameters).getTokenValue(); // Mã hóa token JWT từ JwtEncoderParameters và trả về token
	}
	
	private String createScope(Authentication authentication) { // Tạo scope từ thông tin người dùng sau khi xác thực
		return authentication.getAuthorities().stream() // Lấy danh sách các quyền của người dùng sau khi xác thực
				.map(a -> a.getAuthority()) // Lấy tên của quyền
				.collect(Collectors.joining(" ")); // Nối tất cả các tên quyền lại với nhau và ngăn cách bằng dấu cách
	}
}

record JwtResponse(String token) {}; // Class này dùng để trả về token sau khi xác thực, sử dụng record để tạo class với constructor tự động