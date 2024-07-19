package com.didan.microservices.gatewaysever.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
    return http.authorizeExchange(exchanges ->
        exchanges.pathMatchers(HttpMethod.GET).permitAll() // Cho phép tất cả các request GET không cần xác thực
            .pathMatchers("/didan/accounts/**").authenticated() // Các request đến /didan/accounts/** cần xác thực
            .pathMatchers("/didan/cards/**").authenticated() // Các request đến /didan/cards/** cần xác thực
            .pathMatchers("/didan/loans/**").authenticated()) // Các request đến /didan/loans/** cần xác thực
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Sử dụng JWT để xác thực
        .csrf(csrf -> csrf.disable()) // Tắt CSRF, chỉ nên dùng với trình duyệt tham gia vào quá trình xác thực (Spring MVC)
        .build();
  }
}
