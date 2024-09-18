package com.example.websocket.service;

import com.example.websocket.dto.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class Receiver {

  private final SimpMessageSendingOperations messagingTemplate; // Đối tượng này giúp gửi message tới client subscribe tới /topic/*
  private final SimpUserRegistry simpUserRegistry; // Đối tượng này chứa thông tin về tất cả user đang kết nối tới server

  @KafkaListener(topics = "messaging", groupId = "chat") // Lắng nghe message từ Kafka với topic là "messaging" và groupId là "chat"
  public void consume(Message chatMessage) {
    log.info("Received message from Kafka: {}", chatMessage);
    log.info("User count: {}", simpUserRegistry.getUsers().size());
    for (SimpUser user : simpUserRegistry.getUsers()) { // Duyệt qua tất cả user đang kết nối tới server
      for (SimpSession simpSession : user.getSessions()) { // Duyệt qua tất cả session của user
        log.info("SessionId: {}", simpSession.getId());
        if (!simpSession.getId().equals(chatMessage.getSessionId())) { // Nếu sessionId của message không trùng với sessionId của session thì gửi message tới session đó, mục đích là không gửi lại message tới chính session đã gửi message
          messagingTemplate.convertAndSendToUser(simpSession.getId(), "/topic/public", chatMessage);
        }
      }
    }
  }
}
