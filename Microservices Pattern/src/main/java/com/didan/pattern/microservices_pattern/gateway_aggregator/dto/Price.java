package com.didan.pattern.microservices_pattern.gateway_aggregator.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Price {

  private Integer listPrice;
  private Double discount;
  private Double discountedPrice;
  private Double amountSaved;
  private LocalDate endDate;

}