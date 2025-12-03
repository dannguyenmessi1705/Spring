package com.didan.reactive.redissonstartup.geospatial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class RestaurantUtils {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public static List<RestaurantDto> getRestaurants() {
    InputStream inputStream = RestaurantUtils.class.getClassLoader().getResourceAsStream("restaurant.json"); // Load file restaurant.json từ thư mục resources
    try {
      return objectMapper.readValue(inputStream, new TypeReference<List<RestaurantDto>>() {
      }); // Chuyển đổi nội dung file JSON thành danh sách các đối tượng RestaurantDto
    } catch (IOException ex) {
      log.error("Unable to read restaurants file", ex);
    }
    return Collections.emptyList();
  }

}
