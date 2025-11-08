package com.didan.reactive.learn.updownstream;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import com.didan.reactive.learn.updownstream.dto.UploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductClient {

  private final WebClient client = WebClient.builder()
      .baseUrl("http://localhost:8080")
      .build();

  public Mono<UploadResponse> uploadProducts(Flux<ProductDto> flux) {
    return this.client.post()
        .uri("/products/upload")
        .contentType(MediaType.APPLICATION_NDJSON)
        .body(flux, ProductDto.class)
        .retrieve()
        .bodyToMono(UploadResponse.class);
  }

  public Flux<ProductDto> downloadProducts() {
    return this.client.get()
        .uri("/products/download")
        .accept(MediaType.APPLICATION_NDJSON)
        .retrieve()
        .bodyToFlux(ProductDto.class);
  }

}