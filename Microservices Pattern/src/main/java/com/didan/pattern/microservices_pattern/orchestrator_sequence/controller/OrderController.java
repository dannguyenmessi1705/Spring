package com.didan.pattern.microservices_pattern.orchestrator_sequence.controller;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrderRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrderResponse;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orchestrator-sequence")
@RequiredArgsConstructor
public class OrderController {

  private final OrchestratorService orchestratorService;

  @PostMapping("order")
  public Mono<ResponseEntity<OrderResponse>> placeOrder(@RequestBody Mono<OrderRequest> orderRequest) {
    return orchestratorService.placeOrder(orderRequest)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
