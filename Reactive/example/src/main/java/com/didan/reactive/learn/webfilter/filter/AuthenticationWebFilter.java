package com.didan.reactive.learn.webfilter.filter;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(1) // Filter sẽ qua bộ lọc này đầu tiên
@Component
@RequiredArgsConstructor
public class AuthenticationWebFilter implements WebFilter {

  private final FilterErrorHandler errorHandler; // Sử dụng FilterErrorHandler để xử lý lỗi trong filter
  private static final Map<String, Category> TOKEN_CATEGGORY_MAP = Map.of(
      "secret123", Category.STANDARD,
      "secret456", Category.PRIME
  ); // Vi dụ về ánh xạ token để phân loại người dùng

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var token = exchange.getRequest().getHeaders().getFirst("auth-token"); // Lấy token từ header của yêu cầu

    if (Objects.nonNull(token) && TOKEN_CATEGGORY_MAP.containsKey(token)) {
      exchange.getAttributes().put("category", TOKEN_CATEGGORY_MAP.get(token)); // Lưu category vào attributes của exchange để sử dụng trong các chuỗi lọc tiếp theo
      return chain.filter(exchange); // Nếu token hợp lệ, tiếp tục chuỗi lọc
    }
//    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)); // Nếu token không hợp lệ, trả về lỗi 401 Unauthorized. Sử dụng Mono.fromRunnable để đảm bảo rằng việc thiết lập mã trạng thái được thực hiện khi Mono được đăng ký
    return errorHandler.sendProblemDetail(exchange, HttpStatus.UNAUTHORIZED, "Invalid or missing auth-token"); // Sử dụng FilterErrorHandler để trả về lỗi chi tiết
  }
}
