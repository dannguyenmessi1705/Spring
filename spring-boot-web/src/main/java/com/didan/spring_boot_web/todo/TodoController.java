package com.didan.spring_boot_web.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

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
	public String getAddTodoPage(ModelMap model) {
		String username = (String) model.get("username");
		Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
		model.put("todo", todo);
		return "todo";
	}

	@RequestMapping(value = "add-todo", method = RequestMethod.POST) // Xử lý request POST từ client với URL là
																		// /add-todo
	public String addNewTodo(@Valid Todo todo, BindingResult result, ModelMap model) { // @Valid để kiểm tra dữ liệu
																						// nhập vào, BindingResult để
																						// kiểm tra lỗi
		if (result.hasErrors()) { // Nếu có lỗi thì trả về trang todo để nhập lại
			return "todo"; // Trả về tên của file JSP mà bạn muốn hiển thị (= spring.mvc.view.prefix +
							// "todo" +
							// spring.mvc.view.suffix = /WEB-INF/views/todo.jsp)
		}
		String username = (String) model.get("username"); // Lấy thông tin username từ Session, đã được lưu ở
															// LoginController khi đăng nhập thành công, nếu không có
															// đăng nhập thì sẽ trả về null
		todoService.addTodo(username, todo.getDescription(), LocalDate.now().plusYears(1), false);
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}
	
	@RequestMapping("delete-todo") // Xử lý request GET từ client với URL là /delete-todo
	public String deleteTodo(@RequestParam("id") int id) { // Lấy tham số id từ URL
		todoService.deleteTodo(id); // Xóa Todo theo id
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}

}
