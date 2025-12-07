package com.didan.pattern.microservices_sequence.orchestrator_parallel.client;

import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.ShippingRequest;
import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.ShippingResponse;
import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ShippingClient {

  private final WebClient webClient;
  private static final String SCHEDULE = "schedule";
  private static final String CANCEL = "cancel";

  public ShippingClient(@Value("${orchestrator_parallel.shipping.service}") String shippingServiceUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(shippingServiceUrl)
        .build();
  }

  /**
   * Hàm gọi service Shipping
   *
   * @param endPoint
   * @param request
   * @return
   */
  private Mono<ShippingResponse> callShippingService(String endPoint, ShippingRequest request) {
    return this.webClient
        .post() // Phương thức HTTP POST
        .uri(endPoint) // Đường dẫn endpoint
        .bodyValue(request) // Gửi dữ liệu request trong body
        .retrieve() // Thực hiện yêu cầu và lấy phản hồi
        .bodyToMono(ShippingResponse.class) // Chuyển đổi phản hồi thành Mono<ShippingResponse>
        .onErrorReturn(this.buildErrorResponse(request)); // Xử lý lỗi và trả về phản hồi dự phòng
  }

  /**
   * * Hàm fallback khi gọi service Shipping thất bại
   *
   * @param request
   * @return
   */
  private ShippingResponse buildErrorResponse(ShippingRequest request) {
    return ShippingResponse.create(
        request.getOrderId(),
        request.getQuantity(),
        Status.FAILED,
        null,
        null
    );
  }

  /**
   * * Hàm đặt lịch vận chuyển
   * @param request
   * @return
   */
  public Mono<ShippingResponse> schedule(ShippingRequest request) {
    return this.callShippingService(SCHEDULE, request);
  }

  /**
   * * Hàm hủy lịch vận chuyển
   * @param request
   * @return
   */
  public Mono<ShippingResponse> cancel(ShippingRequest request) {
    return this.callShippingService(CANCEL, request);
  }
}
