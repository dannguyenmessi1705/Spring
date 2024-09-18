package com.example.websocket.config;

import com.example.websocket.dto.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketEventListener {
  private final SimpMessageSendingOperations messagingTemplate;

  // Hàm này sẽ được gọi khi có client kết nối tới server
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    log.info("Received a new web socket connection");
  }

  // Hàm này sẽ được gọi khi có client ngắt kết nối tới server
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage()); // Lấy thông tin từ event
    String username = (String) headerAccessor.getSessionAttributes().get("username"); // Lấy username từ session đã được tạo từ Controller
    if (username != null) { // Nếu username tồn tại (Client đã gửi message "/app/chat.add-user")
      log.info("User Disconnected {}", username);
      Message chatMessage = Message.builder()
          .messageType(Message.MessageType.DISCONNECT)
          .sender(username)
          .build(); // Tạo message DISCONNECT
      messagingTemplate.convertAndSend("/topic/public", chatMessage); // Gửi message tới tất cả client subscribe tới /topic/public
    }
  }
}
