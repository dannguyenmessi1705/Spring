package com.didan.microservices.gatewaysever;

import java.time.LocalTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
                .circuitBreaker(config -> config.setName("accountsCircuitBreaker"))) // Sử dụng Circuit Breaker, tên là accountsCircuitBreaker
            .uri("lb://ACCOUNTS")) // Đường dẫn của microservices cần gọi
        .route(p -> p.path("/didan/loans/**")
            .filters(f -> f.rewritePath("/didan/loans/(?<remaining>.*)", "/${remaining}"))
            .uri("lb://LOANS"))
        .route(p -> p.path("/didan/cards/**")
            .filters(f -> f.rewritePath("/didan/cards/(?<remaining>.*)", "/${remaining}"))
            .uri("lb://CARDS"))
        .build();
  }

}
