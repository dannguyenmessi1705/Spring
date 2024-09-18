package com.example.websocket.controller;

import com.example.websocket.dto.Message;
import com.example.websocket.service.Sender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller // Với WebSocket, ta sẽ sử dụng @Controller thay vì @RestController
@AllArgsConstructor
@Slf4j
public class MessageController {
  private final Sender sender; // Đối tượng này giúp gửi message tới Kafka
  private final SimpMessageSendingOperations messagingTemplate; // Đối tượng này giúp gửi message tới client

  @MessageMapping("/chat.send-message") // Khi client gửi message tới /app/chat.send-message, phương thức này sẽ được gọi
  public void sendMessage(
      @Payload Message chatMessage, // Message được gửi từ client (Sử dụng @Payload để lấy message từ body)
      SimpMessageHeaderAccessor headerAccessor // HeaderAccessor chứa thông tin về message (ví dụ: sessionId, username, ...)
  ) {
    chatMessage.setSessionId(headerAccessor.getSessionId()); // Set sessionId cho message
    sender.send("messaging", chatMessage); // Gửi message tới Kafka
    log.info("Sending message to /topic/public: {}", chatMessage);
    messagingTemplate.convertAndSend("/topic/public", chatMessage); // Gửi message tới tất cả client subscribe tới /topic/public
    log.info("Message sent to /topic/public: {}", chatMessage);
  }

  @MessageMapping("/chat.add-user") // Khi client gửi message tới /app/chat.add-user, phương thức này sẽ được gọi
  @SendTo("/topic/public") // Khi phương thức này được gọi, message sẽ được gửi tới /topic/public thay vì sử dụng SimpMessageSendingOperations
  public Message addUser(
      @Payload Message chatMessage, // Message được gửi từ client (Sử dụng @Payload để lấy message từ body)
      SimpMessageHeaderAccessor headerAccessor // HeaderAccessor chứa thông tin về message (ví dụ: sessionId, username, ...)
  ) {
    if (headerAccessor.getSessionAttributes() != null) { // Nếu session đã được tạo
      headerAccessor.getSessionAttributes().put("username", chatMessage.getSender()); // Set username cho session
    }
    return chatMessage; // Trả về message cho client subscribe tới /topic/public
  }
}
