package com.didan.pattern.microservices_pattern.rate_limiter.client;

import com.didan.pattern.microservices_pattern.rate_limiter.dto.Product;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient client;

  public ProductClient(@Value("${rate_limiter.product.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build(); // Tao WebClient voi baseUrl tu cau hinh
  }

  public Mono<Product> getProduct(Integer id) {
    return this.client
        .get() // Tao GET request
        .uri("{id}", id) // Goi GET den endpoint baseUrl/{id}
        .retrieve() // Thuc hien request va lay response
        .bodyToMono(Product.class) // Chuyen doi body response thanh Mono<ProductResponse>
        .timeout(Duration.ofMillis(500)) // Dat thoi gian timeout la 500ms cho request, neu vuot qua se bi huy request tra ve loi
        .onErrorResume(ex -> Mono.empty()); // Neu co loi (bao gom ca timeout), tra ve Mono rong
  }
}
