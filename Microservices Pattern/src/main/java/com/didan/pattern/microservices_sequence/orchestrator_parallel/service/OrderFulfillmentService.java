package com.didan.pattern.microservices_sequence.orchestrator_parallel.service;

import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.Status;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentService {

  // Inject tất cả các Bean triển khai giao diện Orchestrator (InventoryOrchestrator, PaymentOrchestrator, ShippingOrchestrator)
  private final List<Orchestrator> orchestrators;

  /**
   * Thực hiện quy trình hoàn tất đơn hàng bằng cách gọi đồng thời tất cả các orchestrator_parallel
   * @param ctx
   * @return
   */
  public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx) {
    // Tạo danh sách các Mono từ các orchestrator_parallel
    List<Mono<OrchestrationRequestContext>> list = orchestrators.stream() // Lấy danh sách các orchestrator_parallel
        .map(o -> o.create(ctx)) // Gọi phương thức create của từng orchestrator_parallel để tạo Mono
        .collect(Collectors.toList()); // Thu thập các Mono vào danh sách

    // Sử dụng Mono.zip để kết hợp tất cả các Mono lại với nhau
    return Mono.zip(list, a -> a[0]) // Kết hợp tất cả các Mono, lấy kết quả của Mono đầu tiên (do tất cả đều trả về cùng một context, không tạo mới object nên không cần quan tâm đến kết quả cụ thể của từng Mono)
        .cast(OrchestrationRequestContext.class) // Chuyển đổi kết quả về kiểu OrchestrationRequestContext
        .doOnNext(this::updateStatus); // Cập nhật trạng thái sau khi tất cả orchestrator_parallel hoàn thành
  }

  /**
   * Cập nhật trạng thái tổng thể vào context dựa trên kết quả của tất cả orchestrator_parallel
   * @param ctx
   */
  private void updateStatus(OrchestrationRequestContext ctx) {
    boolean allSuccess = orchestrators.stream().allMatch(o -> o.isSuccess().test(ctx)); // Kiểm tra tất cả orchestrator_parallel có thành công hay không
    Status status = allSuccess ? Status.SUCCESS : Status.FAILED; // Xác định trạng thái tổng thể
    ctx.setStatus(status); // Cập nhật trạng thái vào context
  }
}
