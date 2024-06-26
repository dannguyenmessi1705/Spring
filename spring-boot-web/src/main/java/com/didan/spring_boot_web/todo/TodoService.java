package com.didan.spring_boot_web.todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class TodoService {
	private static List<Todo> todos = new ArrayList<>();

	static {
		todos.add(new Todo(1, "didannguyen", "Learn FullStack", LocalDate.now().plusYears(1), false));
		todos.add(new Todo(2, "didannguyen", "Learn DevOps", LocalDate.now().plusYears(2), false));
		todos.add(new Todo(3, "didannguyen", "Learn CCIE", LocalDate.now().plusYears(3), false));
	} // Khối static này sẽ chạy khi class được load lần đầu tiên vào bộ nhớ

	public List<Todo> getTodosByUsername(String username) {
		Predicate<? super Todo> predicate = todo -> todo.getUsername().equalsIgnoreCase(username); // Tạo một Predicate để kiểm tra username của Todo bằng với username truyền vào
		return todos.stream().filter(predicate).toList(); // Lọc danh sách Todo theo username và trả về danh sách Todo tìm thấy
	} // Lấy danh sách Todo theo username

	public void addTodo(String username, String description, LocalDate date, boolean done) { // Thêm một Todo mới
		Todo todo = new Todo(todos.size() + 1, username, description, date, done);
		todos.add(todo);
	}

	public void deleteTodo(int id) {
		Predicate<? super Todo> predicate = todo -> todo.getId() == id; // Tạo một Predicate để kiểm tra id của Todo
		todos.removeIf(predicate); // Xóa Todo theo id nếu id của Todo bằng với id truyền vào
	}

	public Todo findById(int id) {
		Predicate<? super Todo> predicate = todo -> todo.getId() == id;
		return todos.stream().filter(predicate).findFirst().get(); // Tìm Todo theo id và trả về Todo đầu tiên tìm thấy
																	// hoặc null nếu không tìm thấy
	}

	public void updateTodo(@Valid Todo todo) {
		deleteTodo(todo.getId()); // Xóa Todo cũ
		todos.add(todo); // Thêm Todo mới
	}
}
