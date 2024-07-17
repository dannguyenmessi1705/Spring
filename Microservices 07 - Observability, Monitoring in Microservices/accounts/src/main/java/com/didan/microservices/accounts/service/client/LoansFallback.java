package com.didan.microservices.accounts.service.client;

import com.didan.microservices.accounts.dto.LoansDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoansFallback implements LoansFeignClient { // FallBack class for LoansFeignClient
  @Override
  public ResponseEntity<LoansDto> fetchLoanDetails(String correlationId, String mobileNumber) {
    return null; // Method fallback sẽ trả về null, cache, hoặc giá trị mặc định trong database nếu như service loans không hoạt động
  }
}
