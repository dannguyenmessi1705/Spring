package com.didan.springwebrtc.config;

import com.didan.springwebrtc.interceptor.AuthChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Configuration
public class WebSocketAuthenticationConfig {
  @Bean
  public AuthChannelInterceptor authChannelInterceptor() {
    return new AuthChannelInterceptor();
  }
}
