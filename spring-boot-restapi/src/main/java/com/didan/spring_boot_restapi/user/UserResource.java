package com.didan.spring_boot_restapi.user;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	public ResponseEntity<? super User> getUser(@PathVariable("id") int id) { // Trả về user có id trùng với id truyền vào
		User user = userDAOService.findById(id); // Gọi method findById() từ UserDAOService
		if (user == null) { // Nếu không tìm thấy user thì ném ra exception UserNotFound
			throw new UserNotFound("User not found with id: " + id);
		}
		return new ResponseEntity<>(user, HttpStatus.OK); // Gọi method findById() từ UserDAOService
	}

	@PostMapping(path = "") // Tạo path cho method này là /users
	public ResponseEntity<? super User> createUser(@RequestBody User user) { // Sử dụng @RequestBody để map request body từ
																		// JSON, XML vào User
		// Kiểu trả về của method này là ResponseEntity<? super User> để có thể tùy chỉnh dữ liệu trả về
		User newUser = userDAOService.createUser(user); // Gọi method createUser() từ UserDAOService
		URI location = ServletUriComponentsBuilder.fromCurrentRequest() // Tạo URI cho user mới tạo
				.path("/{id}") // Thêm id của user mới tạo vào URI
				.buildAndExpand(newUser.getId()) // Thêm id của user mới tạo vào URI
				.toUri(); // Chuyển URI thành URI object
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(); // Tạo một MultiValueMap để tạo header cho
																				// ResponseEntity
		headers.add("location", location.toString()); // Thêm key "location" và value là URI của user mới tạo vào
														// header để trả về cho client
		
		return new ResponseEntity<>(newUser, headers, HttpStatus.CREATED); // Trả về 201 Created và URI của user mới tạo trong header	)
	} // Ngoài ra sử dụng @ModelAttribute để map các dữ liệu từ formUrllEncoded,
		// form-data vào User object
	
	@DeleteMapping(path = "{id}") // Tạo path cho method này là /users/{id}" để xóa user có id trùng với id truyền vào
	public void deleteUser(@PathVariable("id") int id) {
		userDAOService.deleteUser(id); // Gọi method deleteUser() từ UserDAOService để xóa user
	}
}
