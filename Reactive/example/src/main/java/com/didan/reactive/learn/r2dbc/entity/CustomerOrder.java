package com.didan.reactive.learn.r2dbc.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_order")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerOrder {

  @Id
  private UUID orderId;

  @Column("customer_id")
  private Integer customerId;

  @Column("product_id")
  private Integer productId;

  @Column("amount")
  private Integer amount;

  @Column("order_date")
  private Instant orderDate;
}
