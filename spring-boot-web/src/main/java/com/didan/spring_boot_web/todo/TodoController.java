package com.didan.spring_boot_web.todo;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

}
