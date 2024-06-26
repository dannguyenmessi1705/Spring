package com.didan.spring_boot_restapi.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component
public class UserDAOService {
	private static List<User> users = new ArrayList<>(); // Tạo một List chứa các user

	static {
		users.add(new User(1, "Adam", LocalDate.now().minusYears(30)));
		users.add(new User(2, "Eva", LocalDate.now().minusYears(28)));
		users.add(new User(3, "Bilton", LocalDate.now().minusYears(50)));
		users.add(new User(4, "Marcus", LocalDate.now().minusYears(11)));
	} // Khởi tạo một số user và thêm vào List khi chương trình chạy

	public List<User> findAll() {
		return users;
	}

	public User findById(int id) {
		Predicate<? super User> predicate = user -> user.getId() == id; // Tạo một Predicate để tìm user có id trùng với
																		// id truyền vào
		return users.stream().filter(predicate).findFirst().orElse(null); // Sử dụng Stream API để tìm user có id trùng
																			// với id truyền vào và trả về user đó, nếu
																			// không tìm thấy thì trả về null
	}
//	
	public User createUser(User user) {
		user.setId(users.size() + 1); // Set id cho user mới);
		users.add(user); // Thêm user mới vào List
		return user; // Trả về user mới
	}
	
	public void deleteUser(int id) {
		Predicate<? super User> predicate = user -> user.getId() == id; // Tạo một Predicate để tìm user có id trùng với
		users.removeIf(predicate); // Xóa user có id trùng với id truyền vào khỏi List nếu tìm thấy
	}

}
