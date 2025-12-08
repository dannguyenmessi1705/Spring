package com.didan.pattern.microservices_pattern.rate_limiter.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Product {

    private Integer id;
    private String category;
    private String description;
    private Integer price;

}