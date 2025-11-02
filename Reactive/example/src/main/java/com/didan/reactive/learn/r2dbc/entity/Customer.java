package com.didan.reactive.learn.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer") // Map class Customer đên bảng "customer" trong cơ sở dữ liệu (Trong R2DBC chỉ có @Table, không có @Entity như JPA)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

  @Id
  private Integer id;

  @Column("name")
  private String name;

  @Column("email")
  private String email;
}
