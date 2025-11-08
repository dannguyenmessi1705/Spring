package com.didan.reactive.learn.updownstream.mapper;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import com.didan.reactive.learn.updownstream.entity.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityDtoMapper {

  public static Product toEntity(ProductDto dto) {
    var product = new Product();
    product.setId(dto.id());
    product.setDescription(dto.description());
    product.setPrice(dto.price());
    return product;
  }

  public static ProductDto toDto(Product product) {
    return new ProductDto(
        product.getId(),
        product.getDescription(),
        product.getPrice()
    );
  }
}
