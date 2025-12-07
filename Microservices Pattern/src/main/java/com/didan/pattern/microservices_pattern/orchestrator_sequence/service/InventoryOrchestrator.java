package com.didan.pattern.microservices_pattern.orchestrator_sequence.service;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.client.InventoryClient;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Status;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InventoryOrchestrator extends Orchestrator {

  private final InventoryClient inventoryClient;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    // Gọi service Inventory để trừ tồn kho (reactive, chưa thực thi ngay khi gọi hàm vì chưa có subscribe)
    return inventoryClient.deduct(ctx.getInventoryRequest()) // Gọi service Inventory để trừ tồn kho
        .doOnNext(ctx::setInventoryResponse) // Cập nhật phản hồi vào context
        .thenReturn(ctx) // Trả về context đã cập nhật
        .handle(this.statusHandler()); // Xử lý trạng thái trả về từ service, nếu thất bại sẽ ném lỗi để kích hoạt quá trình hủy bỏ (rollback), nếu thành công sẽ tiếp tục quy trình
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Status.SUCCESS.equals(ctx.getInventoryResponse().getStatus()); // Kiểm tra trạng thái phản hồi từ service Inventory có thành công hay không
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx) // Tạo Mono từ context hiện tại
        .filter(isSuccess()) // Lọc chỉ những context có trạng thái thành công
        .map(OrchestrationRequestContext::getInventoryRequest) // Lấy yêu cầu tồn kho từ context
        .flatMap(inventoryClient::restore) // Gọi service Inventory để khôi phục tồn kho
        .subscribe(); // Thực thi hành động khôi phục tồn kho (subscribe sẽ kích hoạt luồng reactive mà không cần tác động bên ngoài)
  }
}
