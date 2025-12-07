package com.didan.pattern.microservices_pattern.orchestrator_parallel.util;

import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.InventoryRequest;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.PaymentRequest;
import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.ShippingRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrchestratorUtil {

  /**
   * Hàm xây dựng các request cho các dịch vụ con dựa trên ngữ cảnh yêu cầu điều phối.
   * @param ctx
   */
  public static void buildRequestContext(OrchestrationRequestContext ctx) {
    buildPaymentRequest(ctx);
    buildInventoryRequest(ctx);
    buildShippingRequest(ctx);
  }

  /**
   * Hàm xây dựng yêu cầu thanh toán dựa trên ngữ cảnh yêu cầu điều phối.
   * @param ctx
   */
  private static void buildPaymentRequest(OrchestrationRequestContext ctx) {
    PaymentRequest paymentRequest = PaymentRequest.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getProductPrice() * ctx.getOrderRequest().getQuantity(),
        ctx.getOrderId()
    );
    ctx.setPaymentRequest(paymentRequest);
  }

  /**
   * Hàm xây dựng yêu cầu tồn kho dựa trên ngữ cảnh yêu cầu điều phối.
   * @param ctx
   */
  private static void buildInventoryRequest(OrchestrationRequestContext ctx) {
    InventoryRequest inventoryRequest = InventoryRequest.create(
        ctx.getOrderId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderRequest().getQuantity()
    );
    ctx.setInventoryRequest(inventoryRequest);
  }

  /**
   * Hàm xây dựng yêu cầu vận chuyển dựa trên ngữ cảnh yêu cầu điều phối.
   * @param ctx
   */
  private static void buildShippingRequest(OrchestrationRequestContext ctx) {
    ShippingRequest shippingRequest = ShippingRequest.create(
        ctx.getOrderRequest().getQuantity(),
        ctx.getOrderRequest().getUserId(),
        ctx.getOrderId()
    );
    ctx.setShippingRequest(shippingRequest);
  }
}
