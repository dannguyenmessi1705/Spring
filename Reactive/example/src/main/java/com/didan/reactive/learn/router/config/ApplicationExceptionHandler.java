package com.didan.reactive.learn.router.config;

import com.didan.reactive.learn.validation.exception.CustomerNotFoundException;
import com.didan.reactive.learn.validation.exception.InvalidInputException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Class xử lý ngoại lệ toàn cục cho ứng dụng (không sử dụng @ControllerAdvice vì không dùng controller truyền thống)
 */
@Component
public class ApplicationExceptionHandler {

  /**
   * Xử lý ngoại lệ CustomerNotFoundException và trả về ServerResponse với ProblemDetail khi sử dụng RouterFunction thay cho controller truyền thống
   * @param ex
   * @param request
   * @return
   */
  public Mono<ServerResponse> handleException(CustomerNotFoundException ex, ServerRequest request) {
    var status = HttpStatus.NOT_FOUND;
    var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    problem.setType(URI.create("htpp://example.com/problem/customer-not-found"));
    problem.setTitle("Customer Not Found");
    problem.setInstance(URI.create(request.path()));
    return ServerResponse.status(status).bodyValue(problem);
  }

  /**
   * Xử lý ngoại lệ InvalidInputException và trả về ServerResponse với ProblemDetail khi sử dụng RouterFunction thay cho controller truyền thống
   * @param ex
   * @param serverRequest
   * @return
   */
  public Mono<ServerResponse> handleException(InvalidInputException ex, ServerRequest serverRequest) {
    var status = HttpStatus.BAD_REQUEST;
    var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    problem.setType(URI.create("htpp://example.com/problem/invalid-input"));
    problem.setTitle("Invalid Input");
    problem.setType(URI.create(serverRequest.path()));
    return ServerResponse.status(status).bodyValue(problem);
  }
}
