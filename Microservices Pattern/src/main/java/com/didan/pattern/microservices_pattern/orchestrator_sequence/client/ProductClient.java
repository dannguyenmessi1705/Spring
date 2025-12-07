package com.didan.pattern.microservices_pattern.orchestrator_sequence.client;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient webClient;

  public ProductClient(@Value("${orchestrator_sequence.product.service}") String productServiceUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(productServiceUrl)
        .build();
  }

  public Mono<Product> getProduct(Integer id) {
    return webClient
        .get() // Phương thức HTTP GET
        .uri("{id}", id) // Đường dẫn endpoint với tham số id
        .retrieve() // Thực hiện yêu cầu và lấy phản hồi
        .bodyToMono(Product.class) // Chuyển đổi phản hồi thành Mono<Product>
        .onErrorResume(ex -> Mono.empty()); // Xử lý lỗi và trả về Mono rỗng
  }
}
