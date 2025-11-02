package com.didan.reactive.learn.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {

  @Id
  private Integer id;

  @Column("description")
  private String description;

  @Column("price")
  private Integer price;
}
