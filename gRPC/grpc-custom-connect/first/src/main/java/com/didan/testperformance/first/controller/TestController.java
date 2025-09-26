package com.didan.testperformance.first.controller;

import com.didan.testperformance.first.constant.ProtocolTypeEnum;
import com.didan.testperformance.first.constant.StatusEnum;
import com.didan.testperformance.first.dto.RequestDto;
import com.didan.testperformance.first.service.TestPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@Validated
public class TestController {

  private final TestPerformanceService testPerformanceService;

  @PostMapping(value = "/rest-api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusEnum> testRestApi(@RequestBody @Valid RequestDto requestDto){
    log.info("testRestApi start");
    testPerformanceService.testPerformance(requestDto, ProtocolTypeEnum.REST_API);
    return ResponseEntity.ok(StatusEnum.SUCCESS);
  }

  @PostMapping(value = "/socket", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusEnum> testSocket() {
    return ResponseEntity.ok(StatusEnum.SUCCESS);
  }

  @PostMapping(value = "/grpc", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusEnum> testGrpc(@RequestBody @Valid RequestDto requestDto) {
    log.info("testGrpc start");
    testPerformanceService.testPerformance(requestDto, ProtocolTypeEnum.GRPC);
    return ResponseEntity.ok(StatusEnum.SUCCESS);
  }
}
