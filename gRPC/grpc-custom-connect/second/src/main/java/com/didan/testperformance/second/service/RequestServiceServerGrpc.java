package com.didan.testperformance.second.service;

import com.didan.testperformance.second.constant.ProtocolTypeEnum;
import com.didan.testperformance.second.constant.RequestTypeEnum;
import com.didan.testperformance.second.entity.LogsInfoEntity;
import com.didan.testperformance.second.entity.Request;
import com.didan.testperformance.second.entity.RequestServiceGrpc;
import com.didan.testperformance.second.entity.Response;
import com.didan.testperformance.second.entity.Status;
import com.didan.testperformance.second.repository.LogsInfoRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceServerGrpc extends RequestServiceGrpc.RequestServiceImplBase {

  private final LogsInfoRepository logsInfoRepository;

  @Override
  public void sendRequest(Request request, StreamObserver<Response> responseObserver) {
    log.info("Server received request: {}", request);

    LogsInfoEntity logsInfo = new LogsInfoEntity();
    logsInfo.setRequestId(request.getRequestId());
    logsInfo.setMessage(request.getMessage());
    logsInfo.setRequestType(RequestTypeEnum.RECEIVE);
    logsInfo.setType(ProtocolTypeEnum.GRPC);
    logsInfoRepository.saveAndFlush(logsInfo);

    Response response = Response.newBuilder().setStatus(Status.SUCCESS).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
