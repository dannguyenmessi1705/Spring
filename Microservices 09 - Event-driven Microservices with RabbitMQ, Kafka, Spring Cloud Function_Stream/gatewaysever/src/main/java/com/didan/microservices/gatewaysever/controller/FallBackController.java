package com.didan.microservices.gatewaysever.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallBackController {
  @GetMapping("/contactSupport")
  Mono<String> contactSupport() {
    return Mono.just("An error occurred. Please try after some time or contact support team!!!");
  }
  // Mono là một loại Publisher trong Reactor, nó chỉ phát ra một phần tử hoặc không phát ra phần tử nào
  // dùng để xử lý bất đồng bộ trong Spring WebFlux (Reactive)
}
