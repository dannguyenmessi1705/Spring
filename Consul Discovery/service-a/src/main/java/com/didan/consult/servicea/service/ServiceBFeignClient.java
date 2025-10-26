package com.didan.consult.servicea.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("serviceb")
public interface ServiceBFeignClient {

  @GetMapping(value = "/servicea")
  ResponseEntity<String> getServiceBResponse();

}
