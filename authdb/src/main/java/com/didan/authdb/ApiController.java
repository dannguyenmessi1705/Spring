package com.didan.authdb;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final AuthenticationManager authenticationManager;
	
	public ApiController(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.authenticationManager = authenticationManager;
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
	public ResponseEntity<?> getMethodName(@PathVariable("userId") String userId) {
		Optional<Users> user = userRepository.findById(userId);
		System.out.println(user.get());
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> auth(@RequestBody Login login) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
		return ResponseEntity.ok("User authenticated successfully");
	}
}
