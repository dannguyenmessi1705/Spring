package com.didan.testperformance.first.config.grpc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
public class GrpcServerProperties {

  private long keepAliveTime;
  private boolean keepAliveWithoutCalls;
  private long keepAliveTimeout;
  private String loadBalancingPolicy;
  private int port;
  private long awaitTermination;
  private long permitKeepAliveTime;
  private long handshakeTimeout;
  private int maxInboundMessageSize;
  private int maxInboundMetadataSize;
  private Resource xdsBootstrapConfigPath;
}
