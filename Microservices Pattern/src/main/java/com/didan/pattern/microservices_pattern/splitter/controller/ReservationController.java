package com.didan.pattern.microservices_pattern.splitter.controller;

import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationResponse;
import com.didan.pattern.microservices_pattern.splitter.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("splitter")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping("reserve")
  public Mono<ReservationResponse> reserve(@RequestBody Flux<ReservationItemRequest> flux) {
    return reservationService.reserve(flux);
  }
}
