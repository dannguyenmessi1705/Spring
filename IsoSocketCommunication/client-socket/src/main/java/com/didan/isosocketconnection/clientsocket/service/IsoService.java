package com.didan.isosocketconnection.clientsocket.service;

import com.didan.isosocketconnection.clientsocket.connector.NettyConnector;
import com.didan.isosocketconnection.clientsocket.handler.SynchronizeResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IsoService {

  @Value("${netty.socket.connection.waittimeout:20000}")
  private long waitTimeout;

  private final NettyConnector nettyConnector;

  public ISOMsg getISOMSgFromAPI(ISOMsg request) {
    log.info("Start send request to API");
    SynchronizeResponseHandler handler = new SynchronizeResponseHandler(waitTimeout);
    nettyConnector.onRequest(request, nettyConnector.getClass().getSimpleName(), handler);
    return handler.getResponse();
  }
}
