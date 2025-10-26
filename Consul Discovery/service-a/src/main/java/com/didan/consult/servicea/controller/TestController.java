package com.didan.consult.servicea.controller;

import com.didan.consult.servicea.service.ServiceBFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

  private final ServiceBFeignClient serviceBFeignClient;

  @GetMapping("/gateway")
  public ResponseEntity<String> getGateway() {
    log.info("Received request by gateway server, call service B");
    ResponseEntity<String> response = serviceBFeignClient.getServiceBResponse();
    log.info("Response from service A: {}", response.getBody());
    return ResponseEntity.ok(response.getBody());
  }

  @GetMapping("/serviceb")
  public ResponseEntity<String> getServiceA() {
    log.info("Received request by service B");
    return ResponseEntity.ok("Response from Service A");
  }
}
