package com.didan.elastic.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dannd1
 * @since 7/7/2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdatedEvent {

  private Long productId;
  private LocalDateTime timestamp;
}
