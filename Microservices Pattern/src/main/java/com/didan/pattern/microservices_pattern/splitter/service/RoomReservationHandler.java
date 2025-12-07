package com.didan.pattern.microservices_pattern.splitter.service;

import com.didan.pattern.microservices_pattern.splitter.client.RoomClient;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationType;
import com.didan.pattern.microservices_pattern.splitter.dto.RoomReservationRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.RoomReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RoomReservationHandler extends ReservationHandler {

  private final RoomClient roomClient;

  @Override
  protected ReservationType getType() {
    return ReservationType.ROOM;
  }

  @Override
  protected Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux) {
    return flux.map(this::toRoomRequest)
        .transform(roomClient::resever)
        .map(this::toResponse);
  }

  private RoomReservationRequest toRoomRequest(ReservationItemRequest request) {
    return RoomReservationRequest.create(
        request.getCity(),
        request.getFrom(),
        request.getTo(),
        request.getCategory()
    );
  }

  private ReservationItemResponse toResponse(RoomReservationResponse response) {
    return ReservationItemResponse.create(
        response.getReservationId(),
        this.getType(),
        response.getCategory(),
        response.getCity(),
        response.getCheckin(),
        response.getCheckout(),
        response.getPrice()
    );
  }
}
