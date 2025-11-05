package com.didan.reactive.learn.validation.advice;

import com.didan.reactive.learn.validation.exception.CustomerNotFoundException;
import com.didan.reactive.learn.validation.exception.InvalidInputException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(CustomerNotFoundException.class)
  public ProblemDetail handleException(CustomerNotFoundException ex) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setType(URI.create("htpp://example.com/problem/customer-not-found"));
    problem.setTitle("Customer Not Found");
    return problem;
  }

  @ExceptionHandler(InvalidInputException.class)
  public ProblemDetail handleException(InvalidInputException ex) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setType(URI.create("htpp://example.com/problem/invalid-input"));
    problem.setTitle("Invalid Input");
    return problem;
  }
}
