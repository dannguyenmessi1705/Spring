package com.didan.spring_boot_web.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {
	private final TodoService todoService; // Inject một Bean vào Controller từ Service

	public TodoController(TodoService todoService) {
		super();
		this.todoService = todoService;
	}

	@RequestMapping(value = "list-todos", method = RequestMethod.GET) // Xử lý request GET từ client với URL là
																		// /list-todos
	public String getListTodosByUsername(ModelMap model) {
		List<Todo> todos = todoService.getTodosByUsername("didannguyen"); // Lấy danh sách Todo theo username
		model.addAttribute("todos", todos); // Truyền danh sách Todo sang View để hiển thị lên giao diện
		return "listTodos"; // Trả về tên của file JSP mà bạn muốn hiển thị (= spring.mvc.view.prefix +
							// "listTodos" + spring.mvc.view.suffix = /WEB-INF/views/listTodos.jsp)
	}

	@RequestMapping(value = "add-todo", method = RequestMethod.GET) // Xử lý request GET từ client với URL là /add-todo
	public String getAddTodoPage() {
		return "todo";
	}

	@RequestMapping(value = "add-todo", method = RequestMethod.POST) // Xử lý request POST từ client với URL là
																		// /add-todo
	public String addNewTodo(@RequestParam("description") String description, ModelMap model) {
		String username = (String) model.get("username"); // Lấy thông tin username từ Session, đã được lưu ở
															// LoginController khi đăng nhập thành công, nếu không có
															// đăng nhập thì sẽ trả về null
		todoService.addTodo(username, description, LocalDate.now().plusYears(1), false);
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}

}
