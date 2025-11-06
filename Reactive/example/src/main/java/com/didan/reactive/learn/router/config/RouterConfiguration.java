package com.didan.reactive.learn.router.config;

import com.didan.reactive.learn.validation.exception.CustomerNotFoundException;
import com.didan.reactive.learn.validation.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

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
    return RouterFunctions.route()
        .GET("/customers", routerRequestHandler::getAllCustomers) // Định nghĩa route GET /customers và busniess xử lý yêu cầu
        .GET("/customers/paginated", routerRequestHandler::pagniatedCustomers) // Định nghĩa route GET /customers/paginated và busniess xử lý yêu cầu
        .GET("/customers/{id}", routerRequestHandler::getCustomerById) // Định nghĩa route GET /customers/{id} và busniess xử lý yêu cầu
        .POST("/customers", routerRequestHandler::createCustomer) // Định nghĩa route POST /customers và busniess xử lý yêu cầu
        .PUT("/customers/{id}", routerRequestHandler::updateCustomer) // Định nghĩa route PUT /customers/{id} và busniess xử lý yêu cầu
        .DELETE("/customers/{id}", routerRequestHandler::deleteCustomer) // Định nghĩa route DELETE /customers/{id} và busniess xử lý yêu cầu
        .onError(CustomerNotFoundException.class, globalExceptionHandler::handleException) // Xử lý khi bắt được lỗi CustomerNotFoundException, (exception.class, serverRequest, serverResponse) -> {}
        .onError(InvalidInputException.class, globalExceptionHandler::handleException) // Xử lý khi bắt được lỗi InvalidInputException, (exception.class, serverRequest, serverResponse) -> {}
        .build();
  }

  /**
   * Lưu ý: Nên lưu ý việc sắp xếp thứ tự các route, nên để các route cụ thể (có tham số (parameter) ở dưới cùng để tránh ghi đè các route có path kế thừa).
   * Ví dụ: /customers/{id} nên để dưới cùng sau /customers và /customers/paginated
   * * Nếu không, khi truy cập /customers/paginated sẽ bị hiểu nhầm là truy cập /customers/{id} với id = "paginated"
   */
}
