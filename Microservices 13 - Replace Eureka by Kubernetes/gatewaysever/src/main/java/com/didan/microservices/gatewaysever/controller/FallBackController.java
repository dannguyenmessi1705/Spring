package com.didan.microservices.gatewaysever.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallBackController {

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Value("${spring.config.import}")
  private String configServer;


  @GetMapping("/contactSupport")
  Mono<String> contactSupport() {
    return Mono.just("An error occurred. Please try after some time or contact support team!!!");
  }
  // Mono là một loại Publisher trong Reactor, nó chỉ phát ra một phần tử hoặc không phát ra phần tử nào
  // dùng để xử lý bất đồng bộ trong Spring WebFlux (Reactive)

  @GetMapping("/oauth-value")
  Mono<String> oauthValue() {
    return Mono.just(jwkSetUri);
  }

  @GetMapping("/config-server")
  Mono<String> configServer() {
    return Mono.just(configServer);
  }
}
