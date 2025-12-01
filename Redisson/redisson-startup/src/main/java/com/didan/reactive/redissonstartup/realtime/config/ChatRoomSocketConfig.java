package com.didan.reactive.redissonstartup.realtime.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ChatRoomSocketConfig {

  private final ChatRoomService chatRoomService;

  /**
   * Định nghĩa HandlerMapping để ánh xạ các URL WebSocket đến các WebSocketHandler tương ứng.
   * @return
   */
  @Bean
  public HandlerMapping handlerMapping() {
    Map<String, WebSocketHandler> map = Map.of(
        "/chat",
        chatRoomService
    ); // Ánh xạ URL "/chat" đến ChatRoomService
    return new SimpleUrlHandlerMapping(map, -1); // Tạo và trả về SimpleUrlHandlerMapping với độ ưu tiên -1 (Luu ý: độ ưu tiên càng thấp thì càng được ưu tiên xử lý trước)
  }
}
