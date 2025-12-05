package com.didan.pattern.microservices_pattern.gateway_aggregator.service;

import com.didan.pattern.microservices_pattern.gateway_aggregator.client.ProductClient;
import com.didan.pattern.microservices_pattern.gateway_aggregator.client.PromotionClient;
import com.didan.pattern.microservices_pattern.gateway_aggregator.client.ReviewClient;
import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.Price;
import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.ProductAggregate;
import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.ProductResponse;
import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.PromotionResponse;
import com.didan.pattern.microservices_pattern.gateway_aggregator.dto.Review;
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
  private final PromotionClient promotionClient;
  private final ReviewClient reviewClient;

  public Mono<ProductAggregate> aggregate(Integer id) {
    log.info("Starting aggregation process...");

    return Mono.zip(
            productClient.getProduct(id),
            promotionClient.getPromotion(id),
            reviewClient.getReviews(id)
        ) // Dung zip de ket hop 3 Mono lai thanh mot Mono<Tuple3<>(...)
        .map(t -> toDto(t.getT1(), t.getT2(), t.getT3())); // Chuyen doi Tuple3<> thanh ProductAggregate
  }

  private ProductAggregate toDto(ProductResponse productResponse, PromotionResponse promotionResponse, List<Review> reviews) {
    Price price = new Price();
    var amountSaved = productResponse.getPrice() * promotionResponse.getDiscount() / 100;
    var discountPrice = productResponse.getPrice() - amountSaved;
    price.setListPrice(productResponse.getPrice());
    price.setAmountSaved(amountSaved);
    price.setDiscountedPrice(discountPrice);
    price.setDiscount(promotionResponse.getDiscount());
    price.setEndDate(promotionResponse.getEndDate());
    return ProductAggregate.create(
        productResponse.getId(),
        productResponse.getCategory(),
        productResponse.getDescription(),
        price,
        reviews
    );
  }
}
