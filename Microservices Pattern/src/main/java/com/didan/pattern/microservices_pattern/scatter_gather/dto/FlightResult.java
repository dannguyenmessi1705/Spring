package com.didan.pattern.microservices_pattern.scatter_gather.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class FlightResult {

  private String airline;
  private String from;
  private String to;
  private Double price;
  private LocalDate date;
}
