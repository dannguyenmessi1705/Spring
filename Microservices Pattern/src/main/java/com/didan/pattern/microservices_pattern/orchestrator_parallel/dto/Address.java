package com.didan.pattern.microservices_pattern.orchestrator_parallel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

  String street;
  String city;
  String state;
  String zipCode;
}
