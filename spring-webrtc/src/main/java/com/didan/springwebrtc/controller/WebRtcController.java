package com.didan.springwebrtc.controller;

import static com.didan.springwebrtc.storage.LocalStorage.rooms;
import static com.didan.springwebrtc.storage.LocalStorage.userRooms;

import com.didan.springwebrtc.dto.ChatMessage;
import com.didan.springwebrtc.dto.JoinRoomMessage;
import com.didan.springwebrtc.dto.LeaveRoomMessage;
import com.didan.springwebrtc.dto.RoomMessage;
import com.didan.springwebrtc.dto.RoomUsersMessage;
import com.didan.springwebrtc.dto.WebRtcMessage;
import com.didan.springwebrtc.service.WebRtcService;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebRtcController {

  private final SimpMessagingTemplate messagingTemplate;
  private final WebRtcService webRtcService;

  @MessageMapping("/join") // Phương thức để người dùng tham gia vào một phòng
  public void joinRoom(@Payload JoinRoomMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String username = principal.getName();
    String roomId = message.getRoomId();
    webRtcService.joinRoom(roomId, username);

  }

  @MessageMapping("/leave") // Phương thức để người dùng rời khỏi một phòng
  public void leaveRoom(@Payload LeaveRoomMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String username = principal.getName();
    String roomId = message.getRoomId();

    webRtcService.leaveRoom(roomId, username);
  }

  @MessageMapping("/offer") // Phương thức để gửi offer đến người dùng khác trong phòng
  public void handleOffer(@Payload WebRtcMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String from = principal.getName();
    String to = message.getTo();
    log.info("Offer from {} to {}", from, to);

    // Gửi offer đến người dùng
    messagingTemplate.convertAndSendToUser(to, "/queue/webrtc", new WebRtcMessage("OFFER", from, to, message.getData()));
  }

  @MessageMapping("/answer") // Phương thức để gửi answer đến người dùng khác trong phòng
  public void handleAnswer(@Payload WebRtcMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String from = principal.getName();
    String to = message.getTo();
    log.info("Answer from {} to {}", from, to);

    // Gửi answer đến người dùng
    messagingTemplate.convertAndSendToUser(to, "/queue/webrtc", new WebRtcMessage("ANSWER", from, to, message.getData()));
  }

  @MessageMapping("/ice-candidate") // Phương thức để gửi ICE candidate đến người dùng khác trong phòng
  public void handleIceCandidate(@Payload WebRtcMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String from = principal.getName();
    String to = message.getTo();

    // Gửi ICE candidate đến người dùng
    messagingTemplate.convertAndSendToUser(to, "/queue/webrtc", new WebRtcMessage("ICE_CANDIDATE", from, to, message.getData()));
  }

  @MessageMapping("/chat/room") // Phương thức để gửi tin nhắn chat đến phòng
  public void handleRoomChat(@Payload ChatMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String username = principal.getName();
    String roomId = message.getRoomId();

    // Kiểm tra xem user có trong phòng không - FIX: correct parameter order
    if (!webRtcService.isUserInRoom(username, roomId)) {
      log.warn("User {} is not in room {}", username, roomId);
      return;
    }

    // Tạo chat message với thông tin đầy đủ
    ChatMessage chatMessage = ChatMessage.builder()
        .type("ROOM_CHAT")
        .from(username)
        .content(message.getContent())
        .roomId(roomId)
        .timestamp(java.time.LocalDateTime.now())
        .isPrivate(false)
        .build();

    log.info("Room chat from {} in room {}: {}", username, roomId, message.getContent());

    // Gửi tin nhắn đến tất cả người dùng trong phòng
    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", chatMessage);
  }

  @MessageMapping("/chat/private") // Phương thức để gửi tin nhắn chat riêng tư
  public void handlePrivateChat(@Payload ChatMessage message, Principal principal) {
    if (principal == null) {
      log.error("Principal is null - user not authenticated");
      return;
    }

    String from = principal.getName();
    String to = message.getTo();

    if (to == null || to.trim().isEmpty()) {
      log.warn("Private chat target is empty");
      return;
    }

    // Tạo chat message với thông tin đầy đủ
    ChatMessage chatMessage = ChatMessage.builder()
        .type("PRIVATE_CHAT")
        .from(from)
        .to(to)
        .content(message.getContent())
        .timestamp(java.time.LocalDateTime.now())
        .isPrivate(true)
        .build();

    log.info("Private chat from {} to {}: {}", from, to, message.getContent());

    // Gửi tin nhắn đến người nhận
    messagingTemplate.convertAndSendToUser(to, "/queue/chat", chatMessage);

    // Gửi lại cho người gửi để xác nhận
    messagingTemplate.convertAndSendToUser(from, "/queue/chat", chatMessage);
  }

  // Phương thức để rời khỏi phòng khi người dùng ngắt kết nối
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage()); // Lấy thông tin từ header của kết nối

    Principal user = headerAccessor.getUser();
    if (user == null) {
      log.warn("User is null during disconnect event");
      return;
    }

    String username = user.getName(); // Lấy tên người dùng từ kết nối

    if (username != null) { // Kiểm tra xem người dùng có tên không
      String roomId = userRooms.get(username); // Lấy phòng của người dùng từ bản đồ userRooms
      if (roomId != null) { // Nếu người dùng có phòng, thực hiện rời khỏi phòng
        webRtcService.leaveRoom(roomId, username);
      }
    }
  }
}
