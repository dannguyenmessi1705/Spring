package com.didan.pattern.microservices_pattern.orchestrator_parallel.service;

import com.didan.pattern.microservices_pattern.orchestrator_parallel.client.UserClient;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.Status;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Orchestrator xử lý thanh toán
 */
@Service
@RequiredArgsConstructor
public class PaymentOrchestrator extends Orchestrator {

  private final UserClient userClient;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    // Lưu ý, đây là reactive (chưa có hành động subcribe nên chưa thực thi ngay khi gọi hàm)
    return userClient.deduct(ctx.getPaymentRequest()) // Gọi service User để trừ số dư tài khoản
        .doOnNext(ctx::setPaymentResponse) // Cập nhật phản hồi vào context
        .thenReturn(ctx); // Trả về context đã cập nhật
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    // Kiểm tra trạng thái phản hồi từ service User có thành công hay không
    return ctx -> Status.SUCCESS.equals(ctx.getPaymentResponse().getStatus());
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx) // Tạo Mono từ context hiện tại
        .filter(isSuccess()) // Lọc chỉ những context có trạng thái thành công
        .map(OrchestrationRequestContext::getPaymentRequest) // Lấy yêu cầu thanh toán từ context
        .flatMap(userClient::refund) // Gọi service User để hoàn tiền
        .subscribe(); // Thực thi hành động hoàn tiền (subscribe sẽ kích hoạt luồng reactive mà không cần tác động bên ngoài)
  }
}
