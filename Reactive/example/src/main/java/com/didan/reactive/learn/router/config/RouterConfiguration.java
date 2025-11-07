package com.didan.reactive.learn.router.config;

import com.didan.reactive.learn.validation.exception.CustomerNotFoundException;
import com.didan.reactive.learn.validation.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouterConfiguration {

  private final RouterRequestHandler routerRequestHandler;
  private final ApplicationExceptionHandler globalExceptionHandler;

  /**
   * Bean tạo các route cho ứng dụng (thay cho lớp controller truyền thống)
   *
   * @return
   */
  @Bean
  RouterFunction<ServerResponse> createRouters() {
    return RouterFunctions
        .route()
        .path("customers", this::createRouters2)
        .POST("/customers", routerRequestHandler::createCustomer) // Định nghĩa route POST /customers và busniess xử lý yêu cầu
        .PUT("/customers/{id}", routerRequestHandler::updateCustomer) // Định nghĩa route PUT /customers/{id} và busniess xử lý yêu cầu
        .DELETE("/customers/{id}", routerRequestHandler::deleteCustomer) // Định nghĩa route DELETE /customers/{id} và busniess xử lý yêu cầu
        .GET("/predicate/{number}", RequestPredicates.path("*/1?"), req -> ServerResponse.ok().bodyValue(true)) // Ví dụ về việc sử dụng RequestPredicates để định nghĩa route với điều kiện pathVariable của path có prefix là `1x` (x là 1 ký tự bất kỳ) (VD: 10, 11, 12, ..., 19)
        .onError(CustomerNotFoundException.class, globalExceptionHandler::handleException) // Xử lý khi bắt được lỗi CustomerNotFoundException, (exception.class, serverRequest, serverResponse) -> {}
        .onError(InvalidInputException.class, globalExceptionHandler::handleException) // Xử lý khi bắt được lỗi InvalidInputException, (exception.class, serverRequest, serverResponse) -> {}
        .filter((req, next) -> {
          log.info("Request: {} {}", req.method(), req.uri()); // Log thông tin method và path của request
          log.info("Headers: {}", req.headers().asHttpHeaders()); // Log thông tin header của request
          log.info("Query Params: {}", req.queryParams()); // Log thông tin query param của request
          log.info("Path Variables: {}", req.pathVariables()); // Log thông tin path variable của request

          if (true) { // Ví dụ về việc chặn request (có thể thêm điều kiện tùy ý)
            return next.handle(req); // Tiếp tục xử lý request
          }
          return ServerResponse.status(403).build(); // Chặn request và trả về 403 Forbidden
        })
        .filter((req, next) -> {
          log.info("Filter 2 - Before processing request");
          return next.handle(req);
        })
        .build();
  }

  RouterFunction<ServerResponse> createRouters2() {
    return RouterFunctions.route()
        .GET("paginated", routerRequestHandler::pagniatedCustomers) // Định nghĩa route GET /customers/paginated và busniess xử lý yêu cầu
        .GET("{id}", routerRequestHandler::getCustomerById) // Định nghĩa route GET /customers/{id} và busniess xử lý yêu cầu
        .GET(routerRequestHandler::getAllCustomers) // Định nghĩa route GET /customers và busniess xử lý yêu cầu
        .build();
  }

  /**
   * Lưu ý: Nên lưu ý việc sắp xếp thứ tự các route, nên để các route cụ thể (có tham số (parameter) ở dưới cùng để tránh ghi đè các route có path kế thừa).
   * Ví dụ: /customers/{id} nên để dưới sau /customers/paginated
   * * Nếu không, khi truy cập /customers/paginated sẽ bị hiểu nhầm là truy cập /customers/{id} với id = "paginated"
   */
}
