package com.didan.reactive.redissonstartup.geospatial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestaurantDto {

  private String id;
  private String city;
  private double latitude;
  private double longitude;
  private String name;
  private String zip;
}
