package com.didan.spring_boot_web.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("username") // Lưu trữ thông tin username vào Session để sử dụng ở các request khác, khi có ModelMap put vào key "username"
public class TodoControllerJPA {
	private final TodoService todoService; // Inject một Bean vào Controller từ Service
	private final TodoRepository todoRepository;

	public TodoControllerJPA(TodoService todoService, TodoRepository todoRepository) {
		super();
		this.todoService = todoService;
		this.todoRepository = todoRepository;
	}

	@RequestMapping(value = "list-todos", method = RequestMethod.GET) // Xử lý request GET từ client với URL là
																		// /list-todos
	public String getListTodosByUsername(ModelMap model) {
		String username = getUsernameInSecurity(model); // Lấy thông tin username từ SecurityContextHolder sau khi đăng nhập
		List<Todo> todos = todoRepository.findByUsername(username); // Lấy danh sách Todo theo username
		model.addAttribute("todos", todos); // Truyền danh sách Todo sang View để hiển thị lên giao diện
		return "listTodos"; // Trả về tên của file JSP mà bạn muốn hiển thị (= spring.mvc.view.prefix +
							// "listTodos" + spring.mvc.view.suffix = /WEB-INF/views/listTodos.jsp)
	}

	@RequestMapping(value = "add-todo", method = RequestMethod.GET) // Xử lý request GET từ client với URL là /add-todo
	public String getAddTodoPage(ModelMap model) {
		String username = (String) model.get("username");
		Todo todo = new Todo(0, username, "", LocalDate.now(), false);
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
		todo.setUsername(username); // Cập nhật username cho Todo
		todoRepository.save(todo);
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}

	@RequestMapping("delete-todo") // Xử lý request GET từ client với URL là /delete-todo
	public String deleteTodo(@RequestParam("id") int id) { // Lấy tham số id từ URL
		todoRepository.deleteById(id);
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}

	@RequestMapping(value = "update-todo", method = RequestMethod.GET)
	public String getUpdateTodoPage(@RequestParam("id") int id, ModelMap model) {
		Todo todo = todoRepository.findFirstById(id); // Tìm Todo theo id
		model.put("todo", todo); // Truyền Todo sang View để hiển thị lên giao diện
		return "todo"; // Trả về tên của file JSP mà bạn muốn hiển thị (= spring.mvc.view.prefix +
						// "todo" +
						// spring.mvc.view.suffix = /WEB-INF/views/todo.jsp)
	}

	@RequestMapping(value = "update-todo", method = RequestMethod.POST) // Xử lý request POST từ client với URL là
	// /add-todo
	public String updateTodo(@Valid Todo todo, BindingResult result, ModelMap model) { // @Valid để kiểm tra dữ liệu
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
		todo.setUsername(username); // Cập nhật username cho Todo
		todoRepository.save(todo);
		return "redirect:list-todos"; // Chuyển hướng sang URL /list-todos
	}
	
	private String getUsernameInSecurity(ModelMap model) { // Lấy thông tin username từ SecurityContextHolder sau khi đăng nhập
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Lấy thông tin
																								// Authentication từ SecurityContextHolder
		model.put("username", authentication.getName()); // Lưu thông tin username vào Session
		return authentication.getName(); // Lấy thông tin username từ Authentication
	}

}
