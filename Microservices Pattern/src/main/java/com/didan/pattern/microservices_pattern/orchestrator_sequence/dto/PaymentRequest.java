package com.didan.pattern.microservices_pattern.orchestrator_sequence.dto;

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
public class PaymentRequest {

  Integer userId;
  Integer amount;
  UUID orderId;

}
