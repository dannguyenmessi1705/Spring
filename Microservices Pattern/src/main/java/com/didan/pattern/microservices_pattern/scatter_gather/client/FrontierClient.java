package com.didan.pattern.microservices_pattern.scatter_gather.client;

import com.didan.pattern.microservices_pattern.scatter_gather.dto.FlightResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FrontierClient {

  private final WebClient client;

  public FrontierClient(@Value("${scatter_gather.frontier.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to) {
    return this.client
        .post()
        .bodyValue(FrontierRequest.create(from, to)) // Tao body request
        .retrieve() // lay response
        .bodyToFlux(FlightResult.class) // chuyen doi body ve Flux<FlightResult>
        .onErrorResume(ex -> Mono.empty()); // xu ly loi bang cach tra ve Flux rong (Fallback)
  }

  @Data
  @ToString
  @AllArgsConstructor(staticName = "create")
  private static class FrontierRequest {

    private String from;
    private String to;
  }
}
