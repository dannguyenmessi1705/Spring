package com.didan.pattern.microservices_pattern.gateway_aggregator.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PromotionResponse {

    private Integer id;
    private String type;
    private Double discount;
    private LocalDate endDate;

}