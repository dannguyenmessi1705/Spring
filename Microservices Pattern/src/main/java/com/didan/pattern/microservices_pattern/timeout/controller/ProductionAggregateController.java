package com.didan.pattern.microservices_pattern.timeout.controller;

import com.didan.pattern.microservices_pattern.timeout.dto.ProductAggregate;
import com.didan.pattern.microservices_pattern.timeout.service.ProductionAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway-aggregator")
public class ProductionAggregateController {

  private final ProductionAggregatorService productionAggregatorService;

  @GetMapping("product/{id}")
  public Mono<ResponseEntity<ProductAggregate>> getProductAggregate(@PathVariable Integer id) {
    return this.productionAggregatorService.aggregate(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
