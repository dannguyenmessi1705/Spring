package com.didan.elastic.entity;

import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author dannd1
 * @since 7/3/2025
 */
@Document(indexName = "products")
@Setting(shards = 1, replicas = 0)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDocument {
  @Id
  private String id;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String name;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Double)
  private Double price;

  @Field(type = FieldType.Keyword)
  private String category;

  @Field(type = FieldType.Boolean)
  private Boolean available;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime createdDate;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime updatedDate;

  public ProductDocument(Product product) {
    this.id = product.getId().toString();
    this.name = product.getName();
    this.description = product.getDescription();
    this.price = product.getPrice().doubleValue();
    this.category = product.getCategory();
    this.available = product.getAvailable();
    this.createdDate = product.getCreatedDate();
    this.updatedDate = product.getUpdatedDate();
  }

  public Product toProduct() {
    return Product.builder()
        .id(Long.valueOf(id))
        .name(name)
        .description(description)
        .price(BigDecimal.valueOf(price))
        .category(category)
        .available(available)
        .createdDate(createdDate)
        .updatedDate(updatedDate)
        .build();
  }
}
