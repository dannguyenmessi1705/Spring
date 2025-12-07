package com.didan.pattern.microservices_pattern.splitter.dto;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationResponse {

  UUID reservationId;
  Integer price;
  List<ReservationItemResponse> items;

}
