package com.didan.pattern.microservices_pattern.scatter_gather.service;

import com.didan.pattern.microservices_pattern.scatter_gather.client.DeltaClient;
import com.didan.pattern.microservices_pattern.scatter_gather.client.FrontierClient;
import com.didan.pattern.microservices_pattern.scatter_gather.client.JetBlueClient;
import com.didan.pattern.microservices_pattern.scatter_gather.dto.FlightResult;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSearchService {

  private final DeltaClient deltaClient;
  private final FrontierClient frontierClient;
  private final JetBlueClient jetBlueClient;

  public Flux<FlightResult> getFlights(String from, String to) {
    return Flux.merge(
            deltaClient.getFlights(from, to),
            frontierClient.getFlights(from, to),
            jetBlueClient.getFlights(from, to)
        ) // Su dung merge de hop nhat ket qua tu 3 client
        .take(Duration.ofSeconds(3)); // Gioi han thoi gian cho ket qua ve trong 3 giay (Trong truong hop co loi hoac cham tra ve hoac Server su dung text/event-stream)
  }

}
