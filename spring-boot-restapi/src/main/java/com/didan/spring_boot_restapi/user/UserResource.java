package com.didan.spring_boot_restapi.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "users") // Tạo path cho tất cả các method trong class này là /users
public class UserResource {
	private final UserDAOService userDAOService; // Inject UserDAOService vào UserResource

	public UserResource(UserDAOService userDAOService) {
		this.userDAOService = userDAOService;
	}

	@GetMapping(path = "") // Tạo path cho method này là /users
	public List<User> getAllUsers() { // Trả về tất cả các user
		return userDAOService.findAll(); // Gọi method findAll() từ UserDAOService
	}

	@GetMapping(path = "{id}") // Tạo path cho method này là /users/{id}
	public User getUser(@PathVariable("id") int id) { // Trả về user có id trùng với id truyền vào
		return userDAOService.findById(id); // Gọi method findById() từ UserDAOService
	}

	@PostMapping(path = "") // Tạo path cho method này là /users
	public User createUser(@RequestBody User user) { // Sử dụng @RequestBody để map request body từ JSON, XML vào User
														// object,
		return userDAOService.createUser(user); // Gọi method createUser() từ UserDAOService
	} // Ngoài ra sử dụng @ModelAttribute để map các dữ liệu từ formUrllEncoded,
		// form-data vào User object
}
