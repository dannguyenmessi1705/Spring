package com.didan.pattern.microservices_pattern.orchestrator_sequence.util;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.InventoryRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.PaymentRequest;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.ShippingRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrchestratorUtil {

  /**
   * Hàm xây dựng yêu cầu thanh toán dựa trên ngữ cảnh yêu cầu điều phối.
   *
   * @param ctx
   */
  public static void buildPaymentRequest(OrchestrationRequestContext ctx) {
    PaymentRequest paymentRequest = PaymentRequest.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getProductPrice() * ctx.getOrderRequest().getQuantity(),
        ctx.getOrderId()
    );
    ctx.setPaymentRequest(paymentRequest);
  }

  /**
   * Hàm xây dựng yêu cầu tồn kho dựa trên ngữ cảnh yêu cầu điều phối.
   *
   * @param ctx
   */
  public static void buildInventoryRequest(OrchestrationRequestContext ctx) {
    InventoryRequest inventoryRequest = InventoryRequest.create(
        ctx.getPaymentResponse().getPaymentId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderRequest().getQuantity()
    );
    ctx.setInventoryRequest(inventoryRequest);
  }

  /**
   * Hàm xây dựng yêu cầu vận chuyển dựa trên ngữ cảnh yêu cầu điều phối.
   *
   * @param ctx
   */
  public static void buildShippingRequest(OrchestrationRequestContext ctx) {
    ShippingRequest shippingRequest = ShippingRequest.create(
        ctx.getOrderRequest().getQuantity(),
        ctx.getOrderRequest().getUserId(),
        ctx.getInventoryResponse().getInventoryId()
    );
    ctx.setShippingRequest(shippingRequest);
  }
}
