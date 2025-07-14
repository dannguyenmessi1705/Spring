package com.didan.springwebrtc.interceptor;

import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Component
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    // Lấy StompHeaderAccessor từ message
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) { // Nêu là lệnh CONNECT
      String username = accessor.getFirstNativeHeader("X-Username"); // Lấy tên người dùng từ header
      if (username != null && !username.trim().isEmpty()) {
        // Tạo Principal từ tên người dùng
        Principal principal = new SimplePrincipal(username);
        accessor.setUser(principal); // Đặt Principal vào StompHeaderAccessor
        log.info("User {} connected via WebSocket", username);
      } else {
        log.warn("WebSocket connection attempt without a valid username");
        throw new IllegalArgumentException("WebSocket connection requires a valid username");
      }
    }
    return message;
  }
}
