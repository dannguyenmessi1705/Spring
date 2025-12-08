package com.didan.pattern.microservices_pattern.rate_limiter.controller;

import com.didan.pattern.microservices_pattern.rate_limiter.dto.ProductAggregate;
import com.didan.pattern.microservices_pattern.rate_limiter.service.ProductionAggregatorService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("rate-limiter")
public class ProductionAggregateController {

  private final ProductionAggregatorService productionAggregatorService;

  @GetMapping("product/{id}")
  public Mono<ResponseEntity<ProductAggregate>> getProductAggregate(@PathVariable Integer id) {
    return this.productionAggregatorService.aggregate(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("calculator/{input}")
  // Set giới hạn tốc độ cho phương thức này với tên "calculator-service", nếu vượt quá sẽ gọi phương thức fallback
  @RateLimiter(name = "calculator-service", fallbackMethod = "fallback")
  public Mono<ResponseEntity<Integer>> doubleInput(@PathVariable Integer input) {
    return Mono.fromSupplier(() -> input * 2)
        .map(ResponseEntity::ok);
  }

  public Mono<ResponseEntity<String>> fallback(Integer input, Throwable ex) {
    return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage()));
  }
}
