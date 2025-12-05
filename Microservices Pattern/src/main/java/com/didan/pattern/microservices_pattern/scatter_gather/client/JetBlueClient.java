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
public class JetBlueClient {

  private final WebClient client;
  private static final String JETBLUE = "JETBLUE";

  public JetBlueClient(@Value("${scatter_gather.jetblue.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to) {
    return this.client
        .get()
        .uri("{from}/{to}", from, to)
        .retrieve() // lay response
        .bodyToFlux(FlightResult.class) // chuyen doi body ve Flux<FlightResult>
        .doOnNext(fr -> normalizeResponse(fr, from, to)) // chuan hoa response truoc khi tra ve
        .onErrorResume(ex -> Mono.empty()); // xu ly loi bang cach tra ve Flux rong (Fallback)
  }

  private void normalizeResponse(FlightResult result, String from, String to) {
    result.setFrom(from);
    result.setTo(to);
    result.setAirline(JETBLUE);
  }
}
