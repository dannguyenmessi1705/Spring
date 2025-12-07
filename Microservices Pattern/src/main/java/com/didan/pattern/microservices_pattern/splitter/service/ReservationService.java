package com.didan.pattern.microservices_pattern.splitter.service;

import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemRequest;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationItemResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationResponse;
import com.didan.pattern.microservices_pattern.splitter.dto.ReservationType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

@Service
public class ReservationService {

  private final Map<ReservationType, ReservationHandler> map; // Map chứa các service được implement từ ReservationHandler

  // Inject danh sách các ReservationHandler đã được khai báo trong Spring Context
  public ReservationService(List<ReservationHandler> list) {
    map = list.stream().collect(Collectors.toMap(
        ReservationHandler::getType, // Lấy kiểu ReservationType từ từng handler
        Function.identity() // Lấy chính instance của ReservationHandler
    )); // Chuyển đổi danh sách thành Map với key là ReservationType và value là instance của handler
  }

  public Mono<ReservationResponse> reserve(Flux<ReservationItemRequest> flux) {
    return flux.groupBy(ReservationItemRequest::getType) // Nhóm các yêu cầu theo kiểu đặt chỗ (ReservationType) - Splitter
        .flatMap(this::aggregator) // Gọi aggregator để xử lý từng nhóm yêu cầu - Aggregator
        .collectList() // Thu thập tất cả các ReservationItemResponse vào một danh sách
        .map(this::toResponse); // Chuyển đổi danh sách thành ReservationResponse

  }

  private Flux<ReservationItemResponse> aggregator(GroupedFlux<ReservationType, ReservationItemRequest> groupedFlux) {
    ReservationType type = groupedFlux.key(); // Lấy kiểu đặt chỗ từ nhóm hiện tại
    ReservationHandler handler = map.get(type); // Lấy handler tương ứng từ Map
    return handler.reserve(groupedFlux); // Gọi phương thức reserve của handler để xử lý nhóm yêu cầu
  }

  private ReservationResponse toResponse(List<ReservationItemResponse> list) {
    return ReservationResponse.create(
        UUID.randomUUID(), // Tạo một UUID ngẫu nhiên cho ReservationResponse
        list.stream().mapToInt(ReservationItemResponse::getPrice).sum(), // Tính tổng giá từ danh sách các ReservationItemResponse
        list // Danh sách các mục đặt chỗ
    );
  }

}
