package com.didan.pattern.microservices_pattern.gateway_aggregator.client;

import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.Review;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${gateway_aggregator.review.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build(); // Tao WebClient voi baseUrl tu cau hinh
  }

  public Mono<List<Review>> getReviews(Integer id) {
    return this.client
        .get() // Tao GET request
        .uri("{id}", id) // Goi GET den endpoint baseUrl/{id}
        .retrieve() // Thuc hien request va lay response
        .bodyToFlux(Review.class) // Chuyen doi body response thanh Flux<Review>
        .collectList() // Chuyen doi Flux<Review> thanh Mono<List<Review>>
        .onErrorReturn(Collections.emptyList()); // Neu co loi, tra ve mot List rong
  }
}
