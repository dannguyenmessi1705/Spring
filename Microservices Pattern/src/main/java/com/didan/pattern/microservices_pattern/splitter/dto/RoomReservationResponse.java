package com.didan.pattern.microservices_pattern.splitter.dto;

import java.time.LocalDate;
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
public class RoomReservationResponse {

  UUID reservationId;
  String city;
  LocalDate checkin;
  LocalDate checkout;
  String category;
  Integer price;
}
