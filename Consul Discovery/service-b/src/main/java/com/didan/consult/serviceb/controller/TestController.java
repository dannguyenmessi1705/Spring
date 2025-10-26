package com.didan.consult.serviceb.controller;

import com.didan.consult.serviceb.service.ServiceAFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

  private final ServiceAFeignClient serviceAFeignClient;

  @GetMapping("/gateway")
  public ResponseEntity<String> getGateway() {
    log.info("Received request by gateway server, call service A");
    ResponseEntity<String> response = serviceAFeignClient.getServiceAResponse();
    log.info("Response from service A: {}", response.getBody());
    return ResponseEntity.ok(response.getBody());
  }

  @GetMapping("/servicea")
  public ResponseEntity<String> getServiceB() {
    log.info("Received request by service A");
    return ResponseEntity.ok("Response from Service B");
  }
}
