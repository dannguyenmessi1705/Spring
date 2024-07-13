package com.didan.microservices.gatewaysever.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1) // Annotation thông báo, filter này sẽ được thực thi đầu tiên
@Component // Tạo ra một bean cho Spring Boot quản lý
public class RequestTraceFilter implements GlobalFilter { // GlobalFilter là một interface của Spring Cloud Gateway

  private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

  @Autowired
  FilterUtility filterUtility; // Inject FilterUtility vào RequestTraceFilter

  @Override // Ghi đè phương thức filter của interface GlobalFilter
  // Mono<Void> là một kiểu dữ liệu của Reactor, giúp xử lý bất đồng bộ và trả về một giá trị void
  // ServerWebExchange là một interface của Spring WebFlux, giúp lấy thông tin của request và response
  // GatewayFilterChain là một interface của Spring Cloud Gateway, giúp chuyển tiếp request đến các filter khác
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    HttpHeaders requestHeaders = exchange.getRequest().getHeaders(); // Lấy ra các headers của request
    if (isCorrelationIdPresent(requestHeaders)) { // Kiểm tra xem correlation-id đã tồn tại hay chưa
      logger.debug("Bank-correlation-id found in RequestTraceFilter : {}",
          filterUtility.getCorrelationId(requestHeaders)); // In ra log correlation-id đã tồn tại
    } else { // Nếu correlation-id chưa tồn tại
      String correlationID = generateCorrelationId(); // Tạo ra một correlation-id mới
      exchange = filterUtility.setCorrelationId(exchange, correlationID); // Set correlation-id vào request
      logger.debug("Bank-correlation-id generated in RequestTraceFilter : {}", correlationID); // In ra log correlation-id đã tạo
    }
    return chain.filter(exchange);
  }

  private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
    if (filterUtility.getCorrelationId(requestHeaders) != null) { // Lấy ra correlation-id từ headers
      return true; // Nếu correlation-id đã tồn tại thì trả về true
    } else {
      return false; // Nếu correlation-id chưa tồn tại thì trả về false
    }
  } // Kiểm tra xem correlation-id đã tồn tại hay chưa

  private String generateCorrelationId() {
    return java.util.UUID.randomUUID().toString(); // Tạo ra một correlation-id mới
  }

}