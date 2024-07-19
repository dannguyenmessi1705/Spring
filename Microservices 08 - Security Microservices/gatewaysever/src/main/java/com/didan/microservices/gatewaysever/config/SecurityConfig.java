package com.didan.microservices.gatewaysever.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
    return http.authorizeExchange(exchanges ->
        exchanges.pathMatchers(HttpMethod.GET).permitAll() // Cho phép tất cả các request GET không cần xác thực
            .pathMatchers("/didan/accounts/**").hasRole("ACCOUNTS") // Các request đến /didan/accounts/** cần xác thực với role ACCOUNTS
            .pathMatchers("/didan/cards/**").hasRole("CARDS") // Các request đến /didan/cards/** cần xác thực với role CARDS
            .pathMatchers("/didan/loans/**").hasRole("LOANS")) // Các request đến /didan/loans/** cần xác thực với role LOANS
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(grantedRole ->
            grantedRole.jwtAuthenticationConverter(grantedAuthoritiesExtractor()) // Sử dụng grantedAuthoritiesExtractor để chuyển claim realm_access trong JWT thành GrantedAuthority
        )) // Sử dụng JWT để xác thực
        .csrf(csrf -> csrf.disable()) // Tắt CSRF, chỉ nên dùng với trình duyệt tham gia vào quá trình xác thực (Spring MVC)
        .build();
  }

  // Convert role từ claim realm_access trong JWT thành GrantedAuthority vào trong cấu hình oauth2.jwt
  private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }
}
