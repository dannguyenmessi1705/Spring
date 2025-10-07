package com.didan.testperformance.first.config.grpc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "grpc.xds-enabled", havingValue = "true")
@EnableConfigurationProperties(GrpcProperties.class)
public class XdsBootstrapConfig {

  private final GrpcProperties grpcProperties;

  @PostConstruct
  public void initializeXdsBootstrap() {
    try {
      String bootstrapConfig;
      if (grpcProperties.getServers().getXdsBootstrapConfigPath() != null && grpcProperties.getServers().getXdsBootstrapConfigPath().exists()) {
        Resource resource = grpcProperties.getServers().getXdsBootstrapConfigPath();
        log.info("Loading xDS bootstrap configuration from: {}", resource.getURI());
        bootstrapConfig = resource.getContentAsString(StandardCharsets.UTF_8);
      } else {
        log.info("No xDS bootstrap configuration path provided, using default empty config");
        bootstrapConfig = "{}";
      }
      log.info("Setting xDS bootstrap configuration {}", bootstrapConfig);

      System.setProperty("io.grpc.xds.bootstrapConfig", bootstrapConfig);

      log.info("xDS bootstrap configuration set successfully");
    } catch (IOException e) {
      log.error("Failed to load xDS bootstrap configuration", e);
      throw new RuntimeException("Failed to initialize xDS bootstrap configuration", e);
    }
  }
}