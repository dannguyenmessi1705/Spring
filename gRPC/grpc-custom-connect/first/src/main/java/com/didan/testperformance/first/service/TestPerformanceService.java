package com.didan.testperformance.first.service;

import com.didan.testperformance.first.constant.ProtocolTypeEnum;
import com.didan.testperformance.first.constant.StatusEnum;
import com.didan.testperformance.first.dto.RequestDto;
import org.springframework.http.ResponseEntity;

public interface TestPerformanceService {

  ResponseEntity<StatusEnum> testPerformance(RequestDto requestDto, ProtocolTypeEnum protocolTypeEnum);
}
