package com.didan.pattern.microservices_sequence.orchestrator_parallel.dto;

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
public class OrderResponse {

  Integer userId;
  Integer productId;
  UUID orderId;
  Status status;
  Address shippingAddress;
  String expectedDelivery;

}
