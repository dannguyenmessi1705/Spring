package com.didan.pattern.microservices_pattern.gateway_aggregator.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductResponse {

    private Integer id;
    private String category;
    private String description;
    private Integer price;

}