package com.didan.spring_boot_web.todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

@Service
public class TodoService {
	private static List<Todo> todos = new ArrayList<>();

	static {
		todos.add(new Todo(1, "didannguyen", "Learn FullStack", LocalDate.now().plusYears(1), false));
		todos.add(new Todo(2, "didannguyen", "Learn DevOps", LocalDate.now().plusYears(2), false));
		todos.add(new Todo(3, "didannguyen", "Learn CCIE", LocalDate.now().plusYears(3), false));
	} // Khối static này sẽ chạy khi class được load lần đầu tiên vào bộ nhớ

	public List<Todo> getTodosByUsername(String username) {
		return todos;
	} // Lấy danh sách Todo theo username

	public void addTodo(String username, String description, LocalDate date, boolean done) { // Thêm một Todo mới
		Todo todo = new Todo(todos.size() + 1, username, description, date, done);
		todos.add(todo);
	}

	public void deleteTodo(int id) {
		Predicate<? super Todo> predicate = todo -> todo.getId() == id; // Tạo một Predicate để kiểm tra id của Todo
		todos.removeIf(predicate); // Xóa Todo theo id nếu id của Todo bằng với id truyền vào
	}
}
