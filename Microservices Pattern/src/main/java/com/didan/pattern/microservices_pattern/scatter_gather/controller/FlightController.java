package com.didan.pattern.microservices_pattern.scatter_gather.controller;

import com.didan.pattern.microservices_pattern.scatter_gather.dto.FlightResult;
import com.didan.pattern.microservices_pattern.scatter_gather.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("scatter-gather")
public class FlightController {

  private final FlightSearchService flightSearchService;

  @GetMapping(value = "flights/{from}/{to}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<FlightResult> getFlights(@PathVariable String from, @PathVariable String to) {
    return flightSearchService.getFlights(from, to);
  }
}
