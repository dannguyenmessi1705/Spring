package com.didan.reactive.learn.webfilter.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(2) // Filter sẽ qua bộ lọc này đầu tiên
@Component
@RequiredArgsConstructor
public class AuthorizationWebFilter implements WebFilter {

  private final FilterErrorHandler errorHandler;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var category = exchange.getAttributeOrDefault("category", Category.STANDARD); // Lấy category từ attributes của exchange, nếu không có thì mặc định là STANDARD
    return switch (category) {
      case PRIME -> prime(exchange, chain); // Nếu là người dùng PRIME, gọi phương thức prime
      case STANDARD -> standard(exchange, chain); // Nếu là người dùng STANDARD, gọi phương thức standard
    };
  }

  private Mono<Void> prime(ServerWebExchange exchange, WebFilterChain chain) {
    return chain.filter(exchange); // Cho phép truy cập nếu là người dùng PRIME
  }

  private Mono<Void> standard(ServerWebExchange exchange, WebFilterChain chain) {
    var isGet = HttpMethod.GET.equals(exchange.getRequest().getMethod()); // Kiểm tra xem phương thức yêu cầu có phải là GET không
    if (isGet) {
      return chain.filter(exchange); // Cho phép truy cập nếu là phương thức GET
    }
//    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)); // Nếu không phải GET, trả về lỗi 403 Forbidden
    return errorHandler.sendProblemDetail(exchange, HttpStatus.FORBIDDEN, "Access denied for STANDARD category"); // Sử dụng FilterErrorHandler để trả về lỗi chi tiết
  }
}
