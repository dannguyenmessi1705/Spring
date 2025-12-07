package com.didan.pattern.microservices_pattern.splitter.service;

import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationType;
import reactor.core.publisher.Flux;

public abstract class ReservationHandler {

  protected abstract ReservationType getType();

  protected abstract Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux);
}
