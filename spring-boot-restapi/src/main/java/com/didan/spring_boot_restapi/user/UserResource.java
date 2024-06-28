package com.didan.spring_boot_restapi.user;

// Import tất cả các class trong package WebMvcLinkBuilder (sử dụng linkTO và methodOn cho HATEOAS)
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.didan.spring_boot_restapi.post.Post;
import com.didan.spring_boot_restapi.post.PostRepository;

import jakarta.validation.Valid;

@RestController()
@RequestMapping(path = "users") // Tạo path cho tất cả các method trong class này là /users
public class UserResource {
	private final UserDAOService userDAOService; // Inject UserDAOService vào UserResource
	private final UserRepository userRepository;
	private final PostRepository postRepository;

	public UserResource(UserDAOService userDAOService, UserRepository userRepository, PostRepository postRepository) {
		this.userDAOService = userDAOService;
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	@GetMapping(path = "") // Tạo path cho method này là /users
	public List<User> getAllUsers() { // Trả về tất cả các user
		return userDAOService.findAll(); // Gọi method findAll() từ UserDAOService
	}

	@GetMapping(path = "{id}") // Tạo path cho method này là /users/{id}
	// Sử dụng EntityModel<User> để tùy chỉnh dữ liệu trả về, thêm các link vào dữ liệu trả về (HATEOAS)
	public ResponseEntity<EntityModel<User>> getUser(@PathVariable("id") int id) { // Trả về user có id trùng với id truyền vào
		User user = userDAOService.findById(id); // Gọi method findById() từ UserDAOService
		if (user == null) { // Nếu không tìm thấy user thì ném ra exception UserNotFound
			throw new UserNotFound("User not found with id: " + id);
		}
		EntityModel<User> entityModel = EntityModel.of(user); // Tạo EntityModel<User> từ user chứa dữ liệu trả về
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).getAllUsers()); // Tạo link đến method getAllUsers()
		
		entityModel.add(link.withRel("all-users")); // Thêm link vào dữ liệu trả về với rel là "all-users"
		
		return new ResponseEntity<>(entityModel, HttpStatus.OK); // Trả về dữ liệu trả về
	} 
	// Khi truy cập vào /users/1 sẽ trả về dữ liệu như sau:
	// {
	//     "id": 1,
	//     "name": "Didan",
	//     "birthDate": "2021-08-08T00:00:00.000+00:00",
	//     "_links": {
	//         "all-users": {
	//             "href": "http://localhost:8080/users"
	//         }
	//     }
	// }

	@PostMapping(path = "") // Tạo path cho method này là /users
	public ResponseEntity<? super User> createUser(@Valid @RequestBody User user) { // Sử dụng @RequestBody để map request body từ
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
	
	
	// JPA
	@GetMapping(path = "jpa") // Tạo path cho method này là /users
	public List<User> getAllUsersJPA() { // Trả về tất cả các user
		return userRepository.findAll(); // Gọi method findAll() từ UserDAOService
	}

	@GetMapping(path = "jpa/{id}") // Tạo path cho method này là /users/{id}
	// Sử dụng EntityModel<User> để tùy chỉnh dữ liệu trả về, thêm các link vào dữ liệu trả về (HATEOAS)
	public ResponseEntity<EntityModel<User>> getUserJPA(@PathVariable("id") int id) { // Trả về user có id trùng với id truyền vào
		Optional<User> user = userRepository.findById(id); // Gọi method findById() từ UserDAOService
		// Optional để tránh NullPointerExceptionm, và muốn lấy ra đúng class User thì sử dụng get()
		if (user.isEmpty()) { // Nếu không tìm thấy user thì ném ra exception UserNotFound
			throw new UserNotFound("User not found with id: " + id);
		}
		EntityModel<User> entityModel = EntityModel.of(user.get()); // Tạo EntityModel<User> từ user chứa dữ liệu trả về
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).getAllUsersJPA()); // Tạo link đến method getAllUsers()
		
		entityModel.add(link.withRel("all-users")); // Thêm link vào dữ liệu trả về với rel là "all-users"
		
		return new ResponseEntity<>(entityModel, HttpStatus.OK); // Trả về dữ liệu trả về
	} 
	
	@PostMapping(path = "jpa") // Tạo path cho method này là /users
	public ResponseEntity<? super User> createUserJPA(@Valid @RequestBody User user) { // Sử dụng @RequestBody để map request body từ
																		// JSON, XML vào User
		// Kiểu trả về của method này là ResponseEntity<? super User> để có thể tùy chỉnh dữ liệu trả về
		User newUser = userRepository.save(user); // Gọi method createUser() từ UserDAOService
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
	
	@DeleteMapping(path = "jpa/{id}") // Tạo path cho method này là /users/{id}" để xóa user có id trùng với id truyền vào
	public void deleteUserJPA(@PathVariable("id") int id) {
		userRepository.deleteById(id); // Gọi method deleteUser() từ UserDAOService để xóa user
	}
	
	@GetMapping(path = "jpa/{id}/posts") // Tạo path cho method này là /users/{id}/posts") để lấy tất cả các post của user có id trùng với id truyền vào
	public ResponseEntity<?> getAllPostsUser(@PathVariable("id") int id){
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFound("User not found with id: " + id);
		}
		return new ResponseEntity<>(user.get().getPosts(), HttpStatus.OK);
	}
	
	@PostMapping(path = "jpa/{id}/posts") // Tạo path cho method này là /users/{id}/posts") để tạo post của user có id trùng với id truyền vào
	public ResponseEntity<?> createPostsUser(@PathVariable("id") int id, @Valid @RequestBody Post post){
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFound("User not found with id: " + id);
		}
		post.setUser(user.get());
		postRepository.save(post);
		return new ResponseEntity<>(post, HttpStatus.CREATED);
	}
}
