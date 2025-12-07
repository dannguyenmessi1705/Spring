package com.didan.pattern.microservices_pattern.splitter.client;

import com.didan.pattern.microservices_pattern.splitter.dto.RoomReservationRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.RoomReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RoomClient {

  private final WebClient webClient;

  public RoomClient(@Value("${splitter.room.service}") String baseUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<RoomReservationResponse> resever(Flux<RoomReservationRequest> flux) {
    return webClient
        .post()
        .body(flux, RoomReservationRequest.class)
        .retrieve()
        .bodyToFlux(RoomReservationResponse.class)
        .onErrorResume(ex -> Mono.empty());
  }

}
