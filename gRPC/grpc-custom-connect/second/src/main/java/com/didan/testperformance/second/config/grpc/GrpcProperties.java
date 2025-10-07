package com.didan.testperformance.second.config.grpc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "grpc")
@Getter
@Setter
public class GrpcProperties {

    private GrpcClientProperties clients = new GrpcClientProperties();
    private GrpcServerProperties servers = new GrpcServerProperties();
    private boolean xdsEnabled;
}
