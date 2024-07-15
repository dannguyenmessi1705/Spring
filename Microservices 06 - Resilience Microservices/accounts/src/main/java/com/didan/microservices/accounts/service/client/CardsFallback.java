package com.didan.microservices.accounts.service.client;

import com.didan.microservices.accounts.dto.CardsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient { // FallBack class for CardsFeignClient

  @Override
  public ResponseEntity<CardsDto> fetchCardDetails(String correlationId, String mobileNumber) {
    return null; // Method fallback sẽ trả về null, cache, hoặc giá trị mặc định trong database nếu như service cards không hoạt động
  }
}
