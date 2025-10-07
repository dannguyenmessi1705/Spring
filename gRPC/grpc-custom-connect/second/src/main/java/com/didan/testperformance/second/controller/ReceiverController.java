package com.didan.testperformance.second.controller;

import com.didan.testperformance.second.constant.ProtocolTypeEnum;
import com.didan.testperformance.second.constant.RequestTypeEnum;
import com.didan.testperformance.second.constant.StatusEnum;
import com.didan.testperformance.second.dto.RequestDto;
import com.didan.testperformance.second.entity.LogsInfoEntity;
import com.didan.testperformance.second.repository.LogsInfoRepository;
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
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReceiverController {

  private final LogsInfoRepository logsInfoRepository;

  @PostMapping(value = "/rest-api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusEnum> testRestApi(@RequestBody @Valid RequestDto requestDto){
    log.info("testRestApi receive");
    LogsInfoEntity logsInfo = new LogsInfoEntity();
    logsInfo.setRequestId(requestDto.getRequestId());
    logsInfo.setMessage(requestDto.getMessage());
    logsInfo.setRequestType(RequestTypeEnum.RECEIVE);
    logsInfo.setType(ProtocolTypeEnum.REST_API);

    logsInfoRepository.saveAndFlush(logsInfo);
    return ResponseEntity.ok(StatusEnum.SUCCESS);
  }
}
