package com.didan.authdb;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ApiController {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtEncoder jwtEncoder;
	
	public ApiController(UserRepository userRepository, 
			PasswordEncoder passwordEncoder, 
			RoleRepository roleRepository,
			AuthenticationManager authenticationManager,
			JwtEncoder jwtEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.authenticationManager = authenticationManager;
		this.jwtEncoder = jwtEncoder;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Users user) {
		Users saveUser = new Users(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getDateOfBirth());
		userRepository.save(saveUser);
		return ResponseEntity.ok("User registered successfully");
	}
	
	@PostMapping("/roles/{userId}")
	public ResponseEntity<?> addRoles(@RequestBody Roles role, @PathVariable("userId") String userId) {
		Optional<Users> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		List<Roles> roles = user.get().getRoles();
		
		Roles roleRepo = roleRepository.findByRole(role.getRole());
		if (roleRepo == null) {
			Roles newRole = new Roles(role.getRole());
			newRole.setUser(user.get());
			roleRepository.save(newRole);
			roles.add(newRole);
		}
		else roles.add(roleRepo);
		user.get().setRoles(roles);
		userRepository.save(user.get());
		return ResponseEntity.ok("Roles added successfully");
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<?> getMethodName(@PathVariable("userId") String userId, Authentication authentication) { // Nếu đã xác minh thành công, authentication sẽ chứa thông tin user
		Optional<Users> user = userRepository.findById(userId);
		System.out.println(user.get());
		System.out.println(authentication);
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}
	
	@GetMapping("/decode-token")
	public ResponseEntity<?> decodeToken(HttpServletRequest req) {
		return new ResponseEntity<>(req.getHeader("Authorization"), HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> auth(@RequestBody Login login) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())); // Lấy thông tin user authentication nếu đã xác minh thành công
		return new ResponseEntity<>(new ResponseToken(createToken(auth)), HttpStatus.ACCEPTED);
	}
	
	private String createToken(Authentication authentication) {
		JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
				.issuer("self") // Issuer là người tạo ra token
				.issuedAt(Instant.now()) // Thời gian tạo token
				.expiresAt(Instant.now().plusSeconds(60 * 15)) // Thời gian hết hạn của token
				.subject(authentication.getName()) // Chủ thể của token
				.build(); // Tạo jwtClaimsSet
		JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet); // Tạo jwtEncoderParameters từ jwtClaimsSet
		return jwtEncoder.encode(jwtEncoderParameters).getTokenValue(); // Trả về token
	}
}

record ResponseToken(String token) {};