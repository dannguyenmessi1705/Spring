package com.didan.pattern.microservices_pattern.splitter.service;

import com.didan.pattern.microservices_pattern.splitter.client.CarClient;
import com.didan.pattern.microservices_pattern.splitter.dto.CarReservationRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.CarReservationResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CarReservationHandler extends ReservationHandler {

  private final CarClient carClient;

  @Override
  protected ReservationType getType() {
    return ReservationType.CAR;
  }

  @Override
  protected Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux) {
    return flux.map(this::toCarRequest) // Convert Object về CarReservationRequest
        .transform(carClient::resever) // transform để gọi CarClient đặt xe
        .map(this::toResponse);
  }

  /**
   * Chuyển đổi từ ReservationItemRequest sang CarReservationRequest
   * @param request
   * @return
   */
  private CarReservationRequest toCarRequest(ReservationItemRequest request) {
    return CarReservationRequest.create(
        request.getCity(),
        request.getFrom(),
        request.getTo(),
        request.getCategory()
    );
  }

  /**
   * Chuyển đổi từ CarReservationResponse sang ReservationItemResponse
   * @param response
   * @return
   */
  private ReservationItemResponse toResponse(CarReservationResponse response) {
    return ReservationItemResponse.create(
        response.getReservationId(),
        getType(),
        response.getCategory(),
        response.getCity(),
        response.getPickup(),
        response.getDrop(),
        response.getPrice()
    );
  }
}

/**
 * transform: tương tự như flatMap, nhưng nó cho phép bạn áp dụng một hàm chuyển đổi (transformer function) lên toàn bộ luồng dữ liệu ban đầu mà không cần phải thay đổi cấu trúc của luồng đó.
 */
