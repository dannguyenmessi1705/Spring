package com.didan.testperformance.first.service.impl;

import com.didan.testperformance.first.constant.ProtocolTypeEnum;
import com.didan.testperformance.first.constant.RequestTypeEnum;
import com.didan.testperformance.first.constant.StatusEnum;
import com.didan.testperformance.first.dto.RequestDto;
import com.didan.testperformance.first.entity.LogsInfoEntity;
import com.didan.testperformance.first.entity.Request;
import com.didan.testperformance.first.entity.RequestServiceGrpc;
import com.didan.testperformance.first.entity.Response;
import com.didan.testperformance.first.repository.LogsInfoRepository;
import com.didan.testperformance.first.service.TestPerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestPerformanceServiceImpl implements TestPerformanceService {

  private final LogsInfoRepository logsInfoRepository;
  private final RestTemplate restTemplate;
  private final RequestServiceGrpc.RequestServiceBlockingStub secondServiceBlockingStub;

  @Value("${app.rest.third-url}")
  private String thirdUrl;

  @Override
  public ResponseEntity<StatusEnum> testPerformance(RequestDto requestDto, ProtocolTypeEnum protocolType) {
    LogsInfoEntity logsInfo = new LogsInfoEntity();
    initRequest(logsInfo, requestDto, protocolType);
    StatusEnum finalStatus = StatusEnum.SUCCESS;
    try {
      if (ProtocolTypeEnum.REST_API.equals(protocolType)) {
        log.info("Start call API");
        ResponseEntity<StatusEnum> res = restTemplate.exchange(
            thirdUrl,
            HttpMethod.POST,
            new HttpEntity<>(requestDto, new HttpHeaders()),
            StatusEnum.class
        );
        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() != StatusEnum.SUCCESS) {
          finalStatus = StatusEnum.FAIL;
        }
      } else if (ProtocolTypeEnum.GRPC.equals(protocolType)) {
        Request request = Request.newBuilder()
            .setRequestId(logsInfo.getRequestId())
            .setMessage(logsInfo.getMessage())
            .build();
        Response response = secondServiceBlockingStub.sendRequest(request);
        if (!response.getStatus().toString().equals(StatusEnum.SUCCESS.toString())) {
          finalStatus = StatusEnum.FAIL;
        }
      }
    } catch (Exception ex) {
      log.error("Error testPerformance: ", ex);
      finalStatus = StatusEnum.FAIL;
    }
    savedEndRequest(logsInfo, finalStatus);
    return ResponseEntity.ok(finalStatus);
  }

  private void initRequest(LogsInfoEntity logsInfo, RequestDto requestDto, ProtocolTypeEnum protocolType) {
    logsInfo.setMessage(requestDto.getMessage());
    logsInfo.setType(protocolType);
    logsInfo.setRequestType(RequestTypeEnum.START);
    logsInfoRepository.saveAndFlush(logsInfo);
    log.info("Init log saved: {}", logsInfo);
  }

  private void savedEndRequest(LogsInfoEntity logsInfo, StatusEnum finalStatus) {
    LogsInfoEntity resInfo = new LogsInfoEntity();
    resInfo.setMessage(finalStatus.name());
    resInfo.setType(logsInfo.getType());
    resInfo.setRequestId(logsInfo.getRequestId());
    resInfo.setRequestType(RequestTypeEnum.DONE);
    logsInfoRepository.saveAndFlush(resInfo);
  }
}
