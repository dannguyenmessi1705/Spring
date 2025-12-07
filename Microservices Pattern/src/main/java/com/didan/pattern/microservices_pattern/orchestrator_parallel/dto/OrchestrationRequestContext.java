package com.didan.pattern.microservices_pattern.orchestrator_parallel.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrchestrationRequestContext {

  final UUID orderId = UUID.randomUUID();
  OrderRequest orderRequest;
  Integer productPrice;
  PaymentRequest paymentRequest;
  PaymentResponse paymentResponse;
  InventoryRequest inventoryRequest;
  InventoryResponse inventoryResponse;
  ShippingRequest shippingRequest;
  ShippingResponse shippingResponse;
  Status status;

  public OrchestrationRequestContext(OrderRequest orderRequest) {
    this.orderRequest = orderRequest;
  }
}
