package com.didan.pattern.microservices_pattern.orchestrator_sequence.service;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.client.ProductClient;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Product;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.Status;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.util.OrchestratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentService {

  private final ProductClient productClient;
  private final PaymentOrchestrator paymentOrchestrator;
  private final InventoryOrchestrator inventoryOrchestrator;
  private final ShippingOrchestrator shippingOrchestrator;

  /**
   * Hàm lấy thông tin sản phẩm và cập nhật giá sản phẩm vào context
   *
   * @param ctx
   * @return
   */
  private Mono<OrchestrationRequestContext> getProduct(OrchestrationRequestContext ctx) {
    return productClient
        .getProduct(ctx.getOrderRequest().getProductId())
        .map(Product::getPrice)
        .doOnNext(ctx::setProductPrice)
        .thenReturn(ctx);
  }

  /**
   * Hàm xử lý hoàn tất đơn hàng theo trình tự: Lấy thông tin sản phẩm -> Thanh toán -> Cập nhật tồn kho -> Tạo đơn vận chuyển
   *
   * @param ctx
   * @return
   */
  public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx) {
    return this.getProduct(ctx) // Lấy thông tin sản phẩm và cập nhật giá sản phẩm vào context
        .doOnNext(OrchestratorUtil::buildPaymentRequest) // Xây dựng yêu cầu thanh toán từ thông tin trong context
        .flatMap(paymentOrchestrator::create) // Thực hiện thanh toán
        .doOnNext(OrchestratorUtil::buildInventoryRequest) // Sau khi thanh toán thành công, xây dựng yêu cầu tồn kho từ thông tin trong context
        .flatMap(inventoryOrchestrator::create) // Cập nhật tồn kho
        .doOnNext(OrchestratorUtil::buildShippingRequest) // Sau khi cập nhật tồn kho thành công, xây dựng yêu cầu vận chuyển từ thông tin trong context
        .flatMap(shippingOrchestrator::create) // Tạo đơn vận chuyển
        .doOnNext(c -> c.setStatus(Status.SUCCESS)) // Cập nhật trạng thái thành công vào context khi tất cả bước hoàn thành
        .doOnError(ex -> ctx.setStatus(Status.FAILED)) // Cập nhật trạng thái thất bại vào context nếu có lỗi xảy ra
        .onErrorReturn(ctx); // Trả về context cuối cùng (thành công hoặc thất bại)
  }
}
