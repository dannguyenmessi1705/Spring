package com.didan.microservices.accounts.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.didan.microservices.accounts.dto.ErrorDto;

@ControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		Map<String, String> validationErrors = new HashMap();
		List<ObjectError> validationErrorsList = ex.getBindingResult().getAllErrors();
		
		validationErrorsList.forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			validationErrors.put(fieldName, errorMessage);
		});
		
		return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<? super Exception> handleAllException(
			Exception ex, WebRequest request) {
		return new ResponseEntity<>(new ErrorDto(request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
				LocalDateTime.now()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CustomerAlreadyExistException.class)
	public final ResponseEntity<? super CustomerAlreadyExistException> handleCustomerAlreadyExistException(
			CustomerAlreadyExistException ex, WebRequest request) {
		return new ResponseEntity<>(new ErrorDto(request.getDescription(false), HttpStatus.BAD_REQUEST, ex.getMessage(),
				LocalDateTime.now()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<? super ResourceNotFoundException> handleResourceNotFound(
			ResourceNotFoundException ex, WebRequest request) {
		return new ResponseEntity<>(new ErrorDto(request.getDescription(false), HttpStatus.NOT_FOUND, ex.getMessage(),
				LocalDateTime.now()), HttpStatus.NOT_FOUND);
	}
}
