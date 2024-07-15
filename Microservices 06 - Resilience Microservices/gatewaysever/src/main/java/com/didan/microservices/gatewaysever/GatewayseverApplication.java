package com.didan.microservices.gatewaysever;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.time.LocalTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayseverApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayseverApplication.class, args);
  }

  // Tạo các route cho các microservices
  @Bean // Bean này sẽ được Spring Boot quản lý và sử dụng
  RouteLocator createRouteLocator(RouteLocatorBuilder routeLocatorBuilder) { // RouteLocatorBuilder giúp tạo ra các route
    return routeLocatorBuilder.routes() // Tạo ra các route
        .route(p -> p.path("/didan/accounts/**") // Đường dẫn của route
            .filters(f -> f.rewritePath("/didan/accounts/(?<remaining>.*)", "/${remaining}")  // Rewrite đường dẫn
                .addRequestHeader("X-TIME", LocalTime.now().toString())  // Thêm header vào request
                .circuitBreaker(config -> config.setName("accountsCircuitBreaker") // Sử dụng Circuit Breaker, tên là accountsCircuitBreaker
                    .setFallbackUri("forward:/contactSupport"))) // Nếu có lỗi thì chuyển hướng đến đường dẫn /contactSupport, đã định nghĩa trong Controller
            .uri("lb://ACCOUNTS")) // Đường dẫn của microservices cần gọi
        .route(p -> p.path("/didan/loans/**")
            .filters(f -> f.rewritePath("/didan/loans/(?<remaining>.*)", "/${remaining}")
                .retry(retryConfig -> retryConfig.setRetries(3) // Sử dụng Retry, thử lại 3 lần nếu lần đầu không thành công
                    .setMethods(HttpMethod.GET) // Chỉ thử lại với method GET
                    .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true) // Thời gian giữa các lần thử lại
                    // param 1 - firstBackoff: thời gian mà gateway sẽ chờ trước khi thử lại lần đầu tiên
                    // param 2 - maxBackoff: thời gian tối đa mà gateway sẽ chờ trước khi thử lại lần cuối cùng, nếu vượt quá thì sẽ trả về lỗi (ngăn chặn việc cấp số nhân lên vô hạn)
                    // param 3 - factor: hệ số tăng thời gian giữa các lần thử lại (backoff = backoff * factor)
                    // param 4 - basedOnPreviousValue: true - dựa trên giá trị thời gian giữa các lần thử lại trước đó,
                    //                                 false - dựa trên giá trị thời gian giữa lần thử lại đầu tiên và lần thử lại thứ 2
                ))
            .uri("lb://LOANS"))
        .route(p -> p.path("/didan/cards/**")
            .filters(f -> f.rewritePath("/didan/cards/(?<remaining>.*)", "/${remaining}")
                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                    .setKeyResolver(userKeyResolver())))
            .uri("lb://CARDS"))
        .build();
  }

  // Custom circuit breaker với response time (Mặc định là 1s)
  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()) // Cấu hình mặc định cho tất cả các chức năng
        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()) // Chỉ thay đổi cấu hình timeout response time là 4s so với mặc định 1s
        .build());
  }

  // Rate Limiter
  @Bean
  public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(1, 1, 1);
    // param 1 - defaultReplenishRate: số lần request được phép thực hiện (số lượng tokens được thêm vào thùng bucket) trong 1s (cho biết tốc độ lấp đầy bucket)
    // param 2 - defaultBurstCapacity: số lượng tokens tối đa mà bucket có thể chứa (cho biết dung lượng của bucket), (nếu bằng 0 thì sẽ không có request nào được phép truy cập)
    // param 3 - defaultRequestedTokens: giá mà mỗi 1 request phải trả để truy cập vào hệ thống (mặc định là 1 tokens),
    // tokens sẽ được lấy từ bucket đã được định size ở burstCapacity, cứ sau 1s thì burstCapacity sẽ được thêm số tokens theo replenishRate
    // Ví dụ: param 1 = 1, param 2 = 60, param 3 = 60 => Khi thực hiện 1 request, sẽ trừ 60 tokens từ bucket, sau 1s thì bucket sẽ thêm 1 tokens => 60s sau mới thực hiện request tiếp theo
  }

  // Tạo Bean KeyResolver để tạo chiến lược rate limiter, ví dụ theo user
  @Bean
  public KeyResolver userKeyResolver() {
    return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user")) // Lấy ra user từ headers, tạo chiến lược rate limiter theo user
        .defaultIfEmpty("annoymous"); // Nếu không có user thì mặc định là annoymous (không xác định)
    // Dùng Mono vì trong Gateway là Reactor, mọi thứ đều là bất đồng bộ, Mono giúp xử lý bất đồng bộ và trả về một giá trị
  }

}
