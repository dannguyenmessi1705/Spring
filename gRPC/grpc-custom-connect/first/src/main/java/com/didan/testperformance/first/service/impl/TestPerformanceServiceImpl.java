package com.didan.testperformance.first.service.impl;

import com.didan.testperformance.first.config.grpc.GrpcProperties;
import com.didan.testperformance.first.config.socket.connector.NettyConnector;
import com.didan.testperformance.first.config.socket.handler.SynchronizeResponseHandler;
import com.didan.testperformance.first.constant.ISO8385Message;
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
import com.didan.testperformance.first.util.DateUtil;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Qualifier;
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
  private final GrpcProperties grpcProperties;
  private final RestTemplate restTemplate;
  @Qualifier("clientServiceBlockingStub") private final RequestServiceGrpc.RequestServiceBlockingStub secondServiceBlockingStub;
  private final NettyConnector nettyConnector;
  private Map<String, Long> deadlines;

  @PostConstruct
  public void init() {
    deadlines = grpcProperties.getClients().getDeadlines();
  }

  @Value("${app.rest.third-url}")
  private String thirdUrl;
  @Value("${netty.socket.connection.waittimeout:20000}")
  private long waitTimeout;

  @Override
  public ResponseEntity<StatusEnum> testPerformance(RequestDto requestDto, ProtocolTypeEnum protocolType) {

    LogsInfoEntity logsInfo = new LogsInfoEntity();
    initRequest(logsInfo, requestDto, protocolType);
    StatusEnum finalStatus = StatusEnum.SUCCESS;
    try {
      if (ProtocolTypeEnum.REST_API.equals(protocolType)) {
        log.info("Start call API");
        RequestDto request = new RequestDto();
        request.setRequestId(logsInfo.getRequestId());
        request.setMessage(logsInfo.getMessage());
        ResponseEntity<StatusEnum> res = restTemplate.exchange(
            thirdUrl,
            HttpMethod.POST,
            new HttpEntity<>(request, new HttpHeaders()),
            StatusEnum.class
        );
        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() != StatusEnum.SUCCESS) {
          finalStatus = StatusEnum.FAIL;
        }
      } else if (ProtocolTypeEnum.GRPC.equals(protocolType)) {
        log.info("Start call gRPC");
        Request request = Request.newBuilder()
            .setRequestId(logsInfo.getRequestId())
            .setMessage(logsInfo.getMessage())
            .build();
        Response response = secondServiceBlockingStub.withDeadlineAfter(deadlines.get("default"), TimeUnit.MILLISECONDS).sendRequest(request);
        if (!response.getStatus().toString().equals(StatusEnum.SUCCESS.toString())) {
          finalStatus = StatusEnum.FAIL;
        }
      } else if (ProtocolTypeEnum.SOCKET.equals(protocolType)) {
        log.info("Start call socket");
        ISOMsg isoMsg = new ISOMsg();
        Date transDate = new Date();
        isoMsg.setMTI("0200");
        isoMsg.set(104, requestDto.getMessage());
        isoMsg.set(ISO8385Message.TRANSMISSION_DATETIME, DateUtil.dateToString(transDate, "GMT", "MMddHHmmss")); // Đặt trường ngày giờ truyền
        isoMsg.set(ISO8385Message.SYSTEM_TRACE_AUDIT_NUMBER, "170502"); // Đặt trường số theo dõi hệ thống
        isoMsg.set(ISO8385Message.REFERENCE_NUMBER, logsInfo.getRequestId());
        SynchronizeResponseHandler handler = new SynchronizeResponseHandler(waitTimeout);
        nettyConnector.onRequest(isoMsg, nettyConnector.getClass().getSimpleName(), handler);
        ISOMsg response = handler.getResponse();
        if (response == null || !StatusEnum.SUCCESS.toString().equals(response.getString(104))) {
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
    logsInfo.setRequestId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
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
