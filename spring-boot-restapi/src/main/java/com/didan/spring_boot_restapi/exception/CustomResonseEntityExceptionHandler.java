package com.didan.spring_boot_restapi.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.didan.spring_boot_restapi.user.UserNotFound;

@ControllerAdvice // Annotation này dùng để handle các exception trong tất cả các controller
public class CustomResonseEntityExceptionHandler extends ResponseEntityExceptionHandler { // Kế thừa từ ResponseEntityExceptionHandler để có thể sử dụng các method trong nó
	
	@ExceptionHandler(Exception.class) // Annotation này dùng để handle exception kiểu Exception
	public final ResponseEntity<? super ErrorDetails> handleAllExceptions(Exception ex, WebRequest req) { // Method này trả về ResponseEntity<? super ErrorDetails> để có thể tùy chỉnh dữ liệu trả về
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), req.getDescription(false)); // Tạo một ErrorDetails object để chứa thông tin lỗi
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR); // Trả về 500 Internal Server Error và thông tin lỗi trong ErrorDetails object
	}
	
	@ExceptionHandler(UserNotFound.class)  // Annotation này dùng để handle exception kiểu UserNotFound
	public final ResponseEntity<? super ErrorDetails> handleUserNotFound(UserNotFound ex, WebRequest req) { // Method này trả về ResponseEntity<? super ErrorDetails> để có thể tùy chỉnh dữ liệu trả về
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), req.getDescription(false)); // Tạo một ErrorDetails object để chứa thông tin lỗi
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // Trả về 404 Not Found và thông tin lỗi trong ErrorDetails object
	}
}
