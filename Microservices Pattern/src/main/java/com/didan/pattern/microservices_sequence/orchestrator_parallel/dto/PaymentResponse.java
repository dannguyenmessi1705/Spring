package com.didan.pattern.microservices_sequence.orchestrator_parallel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

  Integer userId;
  String name;
  Integer balance;
  Status status;


}
