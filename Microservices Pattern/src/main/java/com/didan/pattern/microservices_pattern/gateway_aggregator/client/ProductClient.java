package com.didan.pattern.microservices_pattern.gateway_aggregator.client;

import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient client;

  public ProductClient(@Value("${gateway_aggregator.product.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build(); // Tao WebClient voi baseUrl tu cau hinh
  }

  public Mono<ProductResponse> getProduct(Integer id) {
    return this.client
        .get() // Tao GET request
        .uri("{id}", id) // Goi GET den endpoint baseUrl/{id}
        .retrieve() // Thuc hien request va lay response
        .bodyToMono(ProductResponse.class) // Chuyen doi body response thanh Mono<ProductResponse>
        .onErrorResume(ex -> Mono.empty()); // Neu co loi, tra ve Mono rong
  }
}
