package com.didan.springwebrtc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Configuration
@EnableWebSecurity
public class WebSocketSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/ws/**").permitAll() // Allow all requests to WebSocket endpoints)
        .requestMatchers("/**", "/index.html", "/js/**", "/css/**", "/images/**").permitAll() // Allow static resources
        .anyRequest().authenticated()) // Require authentication for all other requests)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(h -> h.frameOptions(FrameOptionsConfig::deny));

    return httpSecurity.build();
  }
}
