package com.didan.consult.serviceb.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("servicea")
public interface ServiceAFeignClient {

  @GetMapping(value = "/serviceb")
  public ResponseEntity<String> getServiceAResponse();

}
