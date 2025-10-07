package com.didan.testperformance.first.config.grpc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "grpc")
@Getter
@Setter
public class GrpcProperties {

    private GrpcServerProperties servers = new GrpcServerProperties();
    private GrpcClientProperties clients = new GrpcClientProperties();
    private boolean xdsEnabled;
}
