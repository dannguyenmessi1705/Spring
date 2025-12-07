package com.didan.pattern.microservices_pattern.splitter.client;

import com.didan.pattern.microservices_pattern.splitter.dto.CarReservationRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.CarReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CarClient {

  private final WebClient webClient;

  public CarClient(@Value("${splitter.car.service}") String baseUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<CarReservationResponse> resever(Flux<CarReservationRequest> flux) {
    return webClient
        .post()
        .body(flux, CarReservationRequest.class)
        .retrieve()
        .bodyToFlux(CarReservationResponse.class)
        .onErrorResume(ex -> Mono.empty());
  }

}
