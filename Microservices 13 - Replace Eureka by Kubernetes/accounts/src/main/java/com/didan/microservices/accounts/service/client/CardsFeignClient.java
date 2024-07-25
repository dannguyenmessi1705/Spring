package com.didan.microservices.accounts.service.client;

import com.didan.microservices.accounts.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cards", url = "http://cards:9000", fallback = CardsFallback.class) // Gọi đến Microservices có name là cards, hiển thị ở Eureka Server, nếu không gọi được thì fallback sẽ được gọi
public interface CardsFeignClient { // Chỉ là Interface, không cần implement
    // Chỉ cần khai báo abstract method, và có signature giống với function cần gọi ở Controller loans
    @GetMapping(value = "/api/fetch", consumes = MediaType.APPLICATION_JSON_VALUE) // Api phải giống với API ở Controller loans
    public ResponseEntity<CardsDto> fetchCardDetails(
        @RequestHeader("bank-correlation-id") String correlationId,
        @RequestParam String mobileNumber);
}
