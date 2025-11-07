package com.didan.reactive.learn.router.config;

import java.util.function.BiFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CalculatorRouteExercise {

  @Bean
  RouterFunction<ServerResponse> createCalculatorRoute() {
    return RouterFunctions.route()
        .path("calculator", this::calculatorRoute)
        .build();
  }

  RouterFunction<ServerResponse> calculatorRoute() {
    return RouterFunctions.route()
        .GET("/{a}/{b}", isOperation("+"), handle((a, b) -> a + b))
        .GET("/{a}/{b}", isOperation("-"), handle((a, b) -> a - b))
        .GET("/{a}/{b}", isOperation("*"), handle((a, b) -> a * b))
        .GET("/{a}/{b}", isOperation("/"), handle((a, b) -> a / b))
        .build();
  }

  RequestPredicate isOperation(String op) {
    return RequestPredicates.headers(h -> op.equals(h.firstHeader("operation")));
  }

  HandlerFunction<ServerResponse> handle(BiFunction<Integer, Integer, Integer> function) {
    return req -> {
      var a = Integer.parseInt(req.pathVariable("a"));
      var b = Integer.parseInt(req.pathVariable("b"));
      var result = function.apply(a, b);
      return ServerResponse.ok().bodyValue(result);
    };
  }

}
