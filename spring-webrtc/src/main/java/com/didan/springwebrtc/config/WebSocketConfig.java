package com.didan.springwebrtc.config;

import com.didan.springwebrtc.interceptor.AuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired
  private AuthChannelInterceptor authChannelInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Kích hoạt cho phép sử dụng các prefix cho các điểm cuối của WebSocket
    registry.enableSimpleBroker("/topic", "/queue");

    // Đặt điểm cuối để gửi tin nhắn đến các client
    registry.setApplicationDestinationPrefixes("/app");

    // Đặt điểm cuối để gửi tin nhắn đến các client cá nhân
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Đăng ký điểm cuối STOMP để kết nối WebSocket
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*") // Cho phép tất cả các nguồn gốc
        .withSockJS(); // Sử dụng SockJS để hỗ trợ các trình duyệt không hỗ trợ WebSocket
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(authChannelInterceptor);
  }
}
