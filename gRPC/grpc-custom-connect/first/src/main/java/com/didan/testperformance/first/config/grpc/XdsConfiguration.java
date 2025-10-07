package com.didan.testperformance.first.config.grpc;

import io.grpc.LoadBalancerRegistry;
import io.grpc.NameResolverRegistry;
import io.grpc.xds.WeightedRoundRobinLoadBalancerProvider;
import io.grpc.xds.XdsNameResolverProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.grpc.config.xds-enabled", havingValue = "true")
public class XdsConfiguration {

    @PostConstruct
    public void registerXdsResolver() {
        NameResolverRegistry.getDefaultRegistry().register(new XdsNameResolverProvider());
        LoadBalancerRegistry.getDefaultRegistry().register(new WeightedRoundRobinLoadBalancerProvider());
    }
}

