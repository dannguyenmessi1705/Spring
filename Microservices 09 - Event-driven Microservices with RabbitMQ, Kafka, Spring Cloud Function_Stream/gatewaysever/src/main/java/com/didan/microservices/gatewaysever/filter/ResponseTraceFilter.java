package com.didan.microservices.gatewaysever.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration // Annotation thông báo, đây là một class cấu hình
public class ResponseTraceFilter { // Class ResponseTraceFilter chứa các phương thức xử lý logic

  private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

  @Autowired
  FilterUtility filterUtility; // Inject FilterUtility vào ResponseTraceFilter

  @Bean // Annotation thông báo, phương thức này sẽ tạo ra một Bean
  public GlobalFilter postGlobalFilter() { // Tạo ra một GlobalFilter để xử lý logic
    return (exchange, chain) -> { // Lambda expression, xử lý logic
      return chain.filter(exchange).then(Mono.fromRunnable(() -> { // Xử lý logic bất đồng bộ
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders(); // Lấy ra các headers của request
        String correlationId = filterUtility.getCorrelationId(requestHeaders); // Lấy ra correlation-id từ headers
        if (!(exchange.getResponse().getHeaders().containsKey(filterUtility.CORRELATION_ID))) { // Kiểm tra xem correlation-id đã tồn tại trong headers của response hay chưa
          logger.debug("Updated the correlation id to the outbound headers: {}", correlationId); // In ra log correlation-id đã được cập nhật
          exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId); // Thêm correlation-id vào headers của response
        }
      }));
    };
  }
}