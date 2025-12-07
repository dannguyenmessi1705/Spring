package com.didan.pattern.microservices_pattern.orchestrator_sequence.client;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.PaymentRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.PaymentResponse;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

  private final WebClient webClient;
  private static final String DEDUCT = "deduct";
  private static final String REFUND = "refund";

  public UserClient(@Value("${orchestrator_sequence.user.service}") String userServiceUrl) {
    this.webClient = WebClient.builder()
        .baseUrl(userServiceUrl)
        .build();
  }

  /**
   * Hàm gọi service User
   *
   * @param endPoint
   * @param request
   * @return
   */
  private Mono<PaymentResponse> callUserService(String endPoint, PaymentRequest request) {
    return webClient
        .post() // Phương thức HTTP POST
        .uri(endPoint) // Đường dẫn endpoint
        .bodyValue(request) // Gửi dữ liệu request trong body
        .retrieve() // Thực hiện yêu cầu và lấy phản hồi
        .bodyToMono(PaymentResponse.class) // Chuyển đổi phản hồi thành Mono<PaymentResponse>
        .onErrorReturn(this.buildErrorResponse(request)); // Xử lý lỗi và trả về phản hồi dự phòng
  }

  /**
   * Hàm fallback khi gọi service User thất bại
   *
   * @param request
   * @return
   */
  private PaymentResponse buildErrorResponse(PaymentRequest request) {
    return PaymentResponse.create(
        null,
        request.getUserId(),
        null,
        request.getAmount(),
        Status.FAILED
    );
  }

  /**
   * Hàm trừ số dư tài khoản
   *
   * @param request
   * @return
   */
  public Mono<PaymentResponse> deduct(PaymentRequest request) {
    return this.callUserService(DEDUCT, request);
  }

  /**
   * Hàm hoàn tiền cho tài khoản
   *
   * @param request
   * @return
   */
  public Mono<PaymentResponse> refund(PaymentRequest request) {
    return this.callUserService(REFUND, request);
  }

}
