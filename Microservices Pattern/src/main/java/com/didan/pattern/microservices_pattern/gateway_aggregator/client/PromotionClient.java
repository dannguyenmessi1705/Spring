package com.didan.pattern.microservices_pattern.gateway_aggregator.client;

import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.PromotionResponse;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PromotionClient {

  private final WebClient client;
  private final PromotionResponse noPromotion = PromotionResponse.create(-1, "NO_PROMOTION", 0.0, LocalDate.now());

  public PromotionClient(@Value("${gateway_aggregator.promotion.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build(); // Tao WebClient voi baseUrl tu cau hinh
  }

  public Mono<PromotionResponse> getPromotion(Integer id) {
    return this.client
        .get() // Tao GET request
        .uri("{id}", id) // Goi GET den endpoint baseUrl/{id}
        .retrieve() // Thuc hien request va lay response
        .bodyToMono(PromotionResponse.class) // Chuyen doi body response thanh Mono<ProductResponse>
        .onErrorReturn(noPromotion); // Neu co loi, tra ve mot PromotionResponse mac dinh (Fallback)
  }
}
