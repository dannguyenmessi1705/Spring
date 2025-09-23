package com.didan.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dannd1
 * @since 7/4/2025
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SyncStatus {

  private long postgresCount;
  private long elasticCount;
  private boolean isSync;
}
