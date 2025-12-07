package com.didan.pattern.microservices_pattern.timeout.service;

import com.didan.pattern.microservices_pattern.timeout.client.ProductClient;
import com.didan.pattern.microservices_pattern.timeout.client.ReviewClient;
import com.didan.pattern.microservices_pattern.timeout.dto.Product;
import com.didan.pattern.microservices_pattern.timeout.dto.ProductAggregate;
import com.didan.pattern.microservices_pattern.timeout.dto.Review;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductionAggregatorService {

  private final ProductClient productClient;
  private final ReviewClient reviewClient;

  public Mono<ProductAggregate> aggregate(Integer id) {
    log.info("Starting aggregation process...");

    return Mono.zip(
            productClient.getProduct(id),
            reviewClient.getReviews(id)
        ) // Dung zip de ket hop 2 Mono lai thanh mot Mono<Tuple2<>(...)
        .map(t -> toDto(t.getT1(), t.getT2())); // Chuyen doi Tuple2<> thanh ProductAggregate
  }

  private ProductAggregate toDto(Product productResponse, List<Review> reviews) {
    return ProductAggregate.create(
        productResponse.getId(),
        productResponse.getCategory(),
        productResponse.getDescription(),
        reviews
    );
  }
}
