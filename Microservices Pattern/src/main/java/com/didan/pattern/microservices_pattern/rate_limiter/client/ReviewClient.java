package com.didan.pattern.microservices_pattern.rate_limiter.client;

import com.didan.pattern.microservices_pattern.rate_limiter.dto.Review;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${rate_limiter.review.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build(); // Tao WebClient voi baseUrl tu cau hinh
  }

  // Su dung RateLimiter voi ten "review-service", neu bi gioi han se goi den phuong thuc fallback
  @RateLimiter(name = "review-service", fallbackMethod = "fallback")
  public Mono<List<Review>> getReviews(Integer id) {
    return this.client
        .get() // Tao GET request
        .uri("{id}", id) // Goi GET den endpoint baseUrl/{id}
        .retrieve() // Thuc hien request va lay response
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty()) // Neu response la loi 4xx, tra ve Mono rong
        .bodyToFlux(Review.class) // Chuyen doi body response thanh Flux<Review>
        .collectList(); // Chuyen doi Flux<Review> thanh Mono<List<Review>>
  }

  public Mono<List<Review>> fallback(Integer id, Throwable ex) {
    return Mono.just(Collections.emptyList());
  }
}
