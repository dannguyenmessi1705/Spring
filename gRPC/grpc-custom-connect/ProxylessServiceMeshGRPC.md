# Giải thích Architecture
## File Bootstrap config chứa địa chỉ của Control Plane (Istio) - nơi gRPC client sẽ kết nối để lấy thông tin service discovery.
> grpc-bootstrap.json
```json
{
  "xds_servers": [
    {
      "server_uri": "istiod.istio-system.svc.cluster.local:15010", // Địa chỉ của Istiod trong Istio
      "channel_creds": [ 
        {
          "type": "insecure"
        }
      ], // Sử dụng kết nối không mã hóa (insecure) vì trong cluster K8s thường không cần mã hóa
      "server_features": ["xds_v3"] // Sử dụng xDS v3
    }
  ],
  "node": {
    "id": "first-service", // ID của node gRPC client 
    "cluster": "test-cluster", // Tên cluster
    "metadata": { // Metadata bổ sung để Istio có thể xác định thông tin của workload
      "WORKLOAD_NAME": "first", // Tên của workload (pod) trong Istio
      "NAMESPACE": "default" // Namespace của workload
    }
  }
}
```
- Vai trò: Chứa địa chỉ của Control Plane (Istio)
- Mục đích: Cho gRPC client biết kết nối đến đâu để lấy thông tin service discovery
- Địa chỉ: istiod.istio-system.svc.cluster.local:15010 (Istio control plane)

> com.didan.testperformance.first.config.grpc.XdsBootstrapConfig.java
```java
package com.didan.testperformance.first.config.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class com.didan.testperformance.first.config.grpc.XdsBootstrapConfig {

    @Value("${grpc.xds.bootstrap-config-path}")
    private Resource bootstrapConfigResource;

    @PostConstruct
    public void initializeXdsBootstrap() {
        try {
            String bootstrapConfig = bootstrapConfigResource.getContentAsString(StandardCharsets.UTF_8);
            log.info("Setting xDS bootstrap configuration");

            // Set the system property that gRPC uses to find the bootstrap config
            System.setProperty("io.grpc.xds.bootstrapConfig", bootstrapConfig);

            log.info("xDS bootstrap configuration set successfully");
        } catch (IOException e) {
            log.error("Failed to load xDS bootstrap configuration", e);
            throw new RuntimeException("Failed to initialize xDS bootstrap configuration", e);
        }
    }
}
```
- Vai trò: Đọc file bootstrap config và thiết lập cấu hình xDS cho gRPC client
- Cách hoạt động: Đọc file từ đường dẫn cấu hình và đặt vào system property

> XdsConfiguration.java
```java
package com.didan.testperformance.first.config.grpc;

import io.grpc.LoadBalancerRegistry;
import io.grpc.NameResolverRegistry;
import io.grpc.xds.WeightedRoundRobinLoadBalancerProvider;
import io.grpc.xds.XdsNameResolverProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XdsConfiguration {

  @PostConstruct
  public void registerXdsResolver() {
    NameResolverRegistry.getDefaultRegistry().register(new XdsNameResolverProvider());
    LoadBalancerRegistry.getDefaultRegistry().register(new WeightedRoundRobinLoadBalancerProvider());
  }
}
```
- Vai trò: Đăng ký xDS name resolver và load balancer vào gRPC client
- Mục đích: Cho phép gRPC client sử dụng xDS để resolve tên service và load balance
- XdsNameResolverProvider: Giúp gRPC client hiểu cách resolve tên service qua xDS
- WeightedRoundRobinLoadBalancerProvider: Cung cấp thuật toán load balancing theo trọng số
- Đăng ký: Được thực hiện trong phương thức annotated với @PostConstruct để đảm bảo nó được gọi sau khi bean được khởi tạo

## ProxylessChannelGrpc sẽ sử dụng xDS target để nói cho gRPC biết rằng nó cần tìm service thông qua xDS discovery, không phải kết nối trực tiếp.
> ProxylessChannelGrpc.java
```java
package com.didan.testperformance.first.config.grpc;

import com.didan.testperformance.first.entity.RequestServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProxylessChannelGrpc {

  @Value("${grpc.xds.service-name}")
  private String xdsServiceName;
  @Value("${grpc.xds.load-balancing-policy}")
  private String loadBalancingPolicy;

  public static final String SECOND_SERVICE = "seconds-service";
  public static final String GZIP_COMPRESSION = "gzip";
  public static final String XDS_SCHEME = "xds:///";
  private final GrpcInterceptor grpcInterceptor;
  @Qualifier("ioExecutor")
  private final Executor executor;

  @Bean
  public RequestServiceGrpc.RequestServiceBlockingStub proxylessSecondServiceBlockingStub() {
    Channel channel = createChannel();
    return RequestServiceGrpc.newBlockingStub(channel);
  }

  private Channel createChannel() {
    String target = buildXdsTarget();
    log.info("Creating xDS proxyless channel to second gRPC server with target: {}", target);
    return ManagedChannelBuilder.forTarget(target)  // Sử dụng forTarget() cho xDS, không phải forAddress()
        .executor(executor)
        .intercept(grpcInterceptor)
        .intercept(gzipInterceptor())
        .defaultLoadBalancingPolicy(loadBalancingPolicy)
        .keepAliveTime(30L, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .keepAliveTimeout(10L, TimeUnit.SECONDS)
        .disableRetry()
        .usePlaintext()
        .build();
  }

  private String buildXdsTarget() {
    // Đây là tên service trong Kubernetes, không phải địa chỉ IP/port cụ thể
    // xDS sẽ resolve tên này thành các endpoint thực tế
    return String.format("%s%s", XDS_SCHEME, xdsServiceName);
  }

  private ClientInterceptor gzipInterceptor() {
    return new ClientInterceptor() {
      @Override
      public <Q, P> ClientCall<Q, P> interceptCall(MethodDescriptor<Q, P> methodDescriptor,
          CallOptions callOptions, Channel channel) {
        return channel.newCall(methodDescriptor, callOptions.withCompression(GZIP_COMPRESSION));
      }
    };
  }
}
```
- Vai trò: Định nghĩa target service mà client muốn kết nối đến
- Không phải địa chỉ IP/port: Đây là tên logic của service
- xDS sẽ resolve: Tên này thành các endpoint thực tế

> application.yml
```yaml
grpc:
  xds:
    service-name: localhost # Tên service trong Kubernetes mà gRPC client muốn kết nối đến
    load-balancing-policy: round_robin # Chính sách load balancing
    bootstrap-config-path: classpath:grpc-bootstrap.json # Đường dẫn đến file bootstrap config
```

## Flow hoạt động
1. gRPC Client khởi tạo với target: "xds:///second-service"
                    ↓
2. gRPC đọc bootstrap config → kết nối đến Istio (istiod:15010)
                    ↓
3. gRPC hỏi Istio: "second-service ở đâu?"
                    ↓
4. Istio trả về danh sách endpoints: 
   - 10.244.1.15:9090
   - 10.244.1.16:9090
   - 10.244.1.17:9090
                    ↓
5. gRPC tự load balance giữa các endpoints này (bypassing sidecar proxy)
