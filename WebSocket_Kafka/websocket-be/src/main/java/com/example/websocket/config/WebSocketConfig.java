package com.example.websocket.config;

import com.example.websocket.service.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final Sender messagingTemplate;
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic"); // Client sẽ subscribe tới route bắt
    // đầu
    // bằng /topic/* và /queue/* để lắng nghe và nhận message từ server
    registry.setApplicationDestinationPrefixes("/app"); // Client sẽ gửi message tới route bắt đầu bằng /app/*
    // /user/{id}/** để gửi message tới user có id tương ứng
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws") // Endpoint để client kết nối tới socket
        .setAllowedOriginPatterns("*") // Cho phép tất cả các origin kết nối tới
        .withSockJS() // Sử dụng SockJS nếu client không hỗ trợ WebSocket
        .setInterceptors(new CustomHandshakeInterceptor(messagingTemplate)); // Sử dụng Interceptor để xử lý
    // handshake
  }
}
