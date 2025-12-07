package com.didan.pattern.microservices_pattern.orchestrator_sequence.service;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Address;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrderRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrderResponse;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Product;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Status;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.util.DebugUtil;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.util.OrchestratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

  private final OrderFulfillmentService orderFulfillmentService;
  private final OrderCancelService orderCancelService;

  public Mono<OrderResponse> placeOrder(Mono<OrderRequest> mono) {
    return mono
        .map(OrchestrationRequestContext::new) // Tạo context từ yêu cầu đặt hàng (OrderRequest)
        .flatMap(orderFulfillmentService::placeOrder) // Thực hiện quy trình đặt hàng bằng Orchestrator
        .doOnNext(this::doOrderPostProcessing) // Xử lý hậu kỳ đơn hàng (nếu cần) (ví dụ: rollback nếu thất bại)
        .doOnNext(DebugUtil::print) // In thông tin context để debug
        .map(this::toOrderResponse); // Chuyển đổi context thành phản hồi đặt hàng (OrderResponse)
  }

  /**
   * Hàm xử lý rollback đơn hàng khi tất cả các bước đã hoàn thành
   *
   * @param ctx
   * @return
   */
  private void doOrderPostProcessing(OrchestrationRequestContext ctx) {
    if (Status.FAILED.equals(ctx.getStatus())) { // Nếu trạng thái là FAILED, thực hiện hủy đơn hàng
      orderCancelService.cancelOrder(ctx); // Gọi dịch vụ hủy đơn hàng
    }
  }

  /**
   * Hàm xử lý tạo response đơn hàng
   *
   * @param ctx
   * @return
   */
  private OrderResponse toOrderResponse(OrchestrationRequestContext ctx) {
    boolean isSuccess = Status.SUCCESS.equals(ctx.getStatus()); // Kiểm tra trạng thái của context để xác định thành công hay thất bại
    Address address = isSuccess ? ctx.getShippingResponse().getAddress() : null; // Lấy địa chỉ giao hàng nếu thành công, ngược lại là null
    String deliveryDate = isSuccess ? ctx.getShippingResponse().getExpectedDelivery() : null; // Lấy ngày giao hàng dự kiến nếu thành công, ngược lại là null
    // Tạo và trả về đối tượng OrderResponse với các thông tin cần thiết
    return OrderResponse.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderId(),
        ctx.getStatus(),
        address,
        deliveryDate
    );

  }
}
