package com.didan.microservices.gatewaysever;

import java.time.Duration;
import java.time.LocalTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayseverApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayseverApplication.class, args);
  }

  // Tạo các route cho các microservices
  @Bean // Bean này sẽ được Spring Boot quản lý và sử dụng
  RouteLocator createRouteLocator(RouteLocatorBuilder routeLocatorBuilder) { // RouteLocatorBuilder giúp tạo ra các route
    return routeLocatorBuilder.routes() // Tạo ra các route
        .route(p -> p.path("/didan/accounts/**") // Đường dẫn của route
            .filters(f -> f.rewritePath("/didan/accounts/(?<remaining>.*)", "/${remaining}")  // Rewrite đường dẫn
                .addRequestHeader("X-TIME", LocalTime.now().toString())  // Thêm header vào request
                .circuitBreaker(config -> config.setName("accountsCircuitBreaker") // Sử dụng Circuit Breaker, tên là accountsCircuitBreaker
                    .setFallbackUri("forward:/contactSupport"))) // Nếu có lỗi thì chuyển hướng đến đường dẫn /contactSupport, đã định nghĩa trong Controller
            .uri("lb://ACCOUNTS")) // Đường dẫn của microservices cần gọi
        .route(p -> p.path("/didan/loans/**")
            .filters(f -> f.rewritePath("/didan/loans/(?<remaining>.*)", "/${remaining}")
                .retry(retryConfig -> retryConfig.setRetries(3) // Sử dụng Retry, thử lại 3 lần nếu lần đầu không thành công
                    .setMethods(HttpMethod.GET) // Chỉ thử lại với method GET
                    .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true) // Thời gian giữa các lần thử lại
                    // param 1 - firstBackoff: thời gian mà gateway sẽ chờ trước khi thử lại lần đầu tiên
                    // param 2 - maxBackoff: thời gian tối đa mà gateway sẽ chờ trước khi thử lại lần cuối cùng, nếu vượt quá thì sẽ trả về lỗi (ngăn chặn việc cấp số nhân lên vô hạn)
                    // param 3 - factor: hệ số tăng thời gian giữa các lần thử lại (backoff = backoff * factor)
                    // param 4 - basedOnPreviousValue: true - dựa trên giá trị thời gian giữa các lần thử lại trước đó,
                    //                                 false - dựa trên giá trị thời gian giữa lần thử lại đầu tiên và lần thử lại thứ 2
                ))
            .uri("lb://LOANS"))
        .route(p -> p.path("/didan/cards/**")
            .filters(f -> f.rewritePath("/didan/cards/(?<remaining>.*)", "/${remaining}"))
            .uri("lb://CARDS"))
        .build();
  }

}
