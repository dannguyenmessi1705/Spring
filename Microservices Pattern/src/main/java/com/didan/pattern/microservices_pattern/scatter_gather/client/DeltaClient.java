package com.didan.pattern.microservices_pattern.scatter_gather.client;

import com.didan.pattern.microservices_pattern.scatter_gather.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DeltaClient {

  private final WebClient client;

  public DeltaClient(@Value("${scatter_gather.delta.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to) {
    return this.client
        .get()
        .uri("{from}/{to}", from, to) // goi den uri basedUrl/{from}/{to}
        .retrieve() // lay response
        .bodyToFlux(FlightResult.class) // chuyen doi body ve Flux<FlightResult>
        .onErrorResume(ex -> Mono.empty()); // xu ly loi bang cach tra ve Flux rong (Fallback)
  }

}
