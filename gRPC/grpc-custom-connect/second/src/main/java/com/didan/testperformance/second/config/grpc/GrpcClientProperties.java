package com.didan.testperformance.second.config.grpc;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrpcClientProperties {

  private String host;
  private int port;
  private long keepAliveTime;
  private boolean keepAliveWithoutCalls;
  private long keepAliveTimeout;
  private Map<String, Long> deadlines;
  private String xdsNameService;
}
