package com.didan.consult.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayServerApplication.class, args);
  }

  @Bean
  RouteLocator createRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder.routes()
        .route(p -> p.path("/servicea/**")
            .filters(f -> f.rewritePath("/servicea/(?<remaining>.*)", "/${remaining}"))
            .uri("lb://SERVICEA")
        )
        .route(p -> p.path("/serviceb/**")
            .filters(f -> f.rewritePath("/serviceb/(?<remaining>.*)", "/${remaining}"))
            .uri("lb://SERVICEB")
        )
        .build();
  }
}
