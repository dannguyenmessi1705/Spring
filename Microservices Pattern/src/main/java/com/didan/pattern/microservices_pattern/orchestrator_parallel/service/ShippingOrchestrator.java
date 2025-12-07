package com.didan.pattern.microservices_pattern.orchestrator_parallel.service;

import com.didan.pattern.microservices_pattern.orchestrator_parallel.client.ShippingClient;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.Status;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShippingOrchestrator extends Orchestrator {

  private final ShippingClient shippingClient;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    // Gọi service Shipping để lên lịch giao hàng (reactive, chưa thực thi ngay khi gọi hàm vì chưa có subscribe)
    return shippingClient.schedule(ctx.getShippingRequest()) // Gọi service Shipping để lên lịch giao hàng
        .doOnNext(ctx::setShippingResponse) // Cập nhật phản hồi vào context
        .thenReturn(ctx); // Trả về context đã cập nhật
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Status.SUCCESS.equals(ctx.getShippingResponse().getStatus()); // Kiểm tra trạng thái phản hồi từ service Shipping có thành công hay không
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx) // Tạo Mono từ context hiện tại
        .filter(isSuccess()) // Lọc chỉ những context có trạng thái thành công
        .map(OrchestrationRequestContext::getShippingRequest) // Lấy yêu cầu vận chuyển từ context
        .flatMap(shippingClient::cancel) // Gọi service Shipping để hủy lịch giao hàng
        .subscribe(); // Thực thi hành động hủy lịch giao hàng (subscribe sẽ kích hoạt luồng reactive mà không cần tác động bên ngoài)
  }
}
