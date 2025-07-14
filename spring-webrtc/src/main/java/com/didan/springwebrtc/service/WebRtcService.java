package com.didan.springwebrtc.service;

import static com.didan.springwebrtc.storage.LocalStorage.rooms;
import static com.didan.springwebrtc.storage.LocalStorage.userConnections;
import static com.didan.springwebrtc.storage.LocalStorage.userRooms;

import com.didan.springwebrtc.dto.RoomMessage;
import com.didan.springwebrtc.dto.RoomUsersMessage;
import com.didan.springwebrtc.storage.LocalStorage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebRtcService {

  private final SimpMessagingTemplate messagingTemplate;

  /**
   * User tham gia phòng
   *
   * @param roomId
   * @param username
   * @return
   */
  public synchronized boolean joinRoom(String roomId, String username) {
    // Kiểm tra user đã ở room khác chưa
    String currentRoom = userRooms.get(username);
    if (currentRoom != null && !currentRoom.equals(roomId)) {
      leaveRoom(currentRoom, username);
    }

    // Kiểm tra giới hạn user trong room (tối đa 10 người)
    Set<String> usersInRoom = rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
    if (usersInRoom.size() >= 10) {
      log.warn("Room {} is full, cannot add user {}", roomId, username);
      return false;
    }

    usersInRoom.add(username);
    userRooms.put(username, roomId);
    userConnections.put(username, LocalDateTime.now());

    // Gửi thông báo cho tất cả người dùng trong phòng
    messagingTemplate.convertAndSend("/topic/room/" + roomId, new RoomMessage("USER_JOINED", username, null));

    // Gửi danh sách người dùng trong phòng
    Set<String> currentUsersInRoom = rooms.get(roomId);
    messagingTemplate.convertAndSendToUser(username, "/queue/room-users", new RoomUsersMessage(roomId, currentUsersInRoom));

    log.info("User {} joined room {} (total users: {})", username, roomId, usersInRoom.size());
    return true;
  }

  /**
   * User rời khỏi phòng
   *
   * @param roomId
   * @param username
   */
  public synchronized void leaveRoom(String roomId, String username) {
    Set<String> usersInRoom = rooms.get(roomId);
    if (usersInRoom != null) {
      usersInRoom.remove(username);

      // Thông báo cho các user khác
      messagingTemplate.convertAndSend("/topic/room/" + roomId,
          new RoomMessage("USER_LEFT", username, null));

      // Xóa room nếu trống
      if (usersInRoom.isEmpty()) {
        rooms.remove(roomId);
        log.info("Room {} removed (empty)", roomId);
      }
    }

    userRooms.remove(username);
    userConnections.remove(username);

    log.info("User {} left room {}", username, roomId);
  }

  /**
   * Lấy danh sách người dùng trong phòng
   *
   * @param roomId
   * @return
   */
  public Set<String> getUsersInRoom(String roomId) {
    return rooms.getOrDefault(roomId, Collections.emptySet());
  }

  /**
   * Lấy phòng của user
   *
   * @param username
   * @return
   */
  public String getUserRoom(String username) {
    return userRooms.get(username);
  }

  /**
   * Kiểm tra xem user có trong phòng không
   *
   * @param username
   * @param roomId
   * @return
   */
  public boolean isUserInRoom(String username, String roomId) {
    return roomId.equals(userRooms.get(username));
  }

  /**
   * Lấy thông tin thống kê của phòng
   *
   * @param roomId
   * @return
   */
  public Map<String, Object> getRoomStats(String roomId) {
    Set<String> users = getUsersInRoom(roomId);
    return Map.of(
        "roomId", roomId,
        "userCount", users.size(),
        "users", users,
        "createdAt", Objects.requireNonNull(users.isEmpty() ? null :
            userConnections.values().stream()
                .min(LocalDateTime::compareTo)
                .orElse(null))
    );
  }


  /**
   * Dọn dẹp các kết nối không hoạt động trong 30 phút
   */
  @Scheduled(fixedRate = 300000) // 5 phút
  public void cleanupInactiveConnections() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

    userConnections.entrySet().removeIf(entry -> {
      if (entry.getValue().isBefore(cutoff)) {
        String username = entry.getKey();
        String roomId = userRooms.get(username);
        if (roomId != null) {
          leaveRoom(roomId, username);
        }
        log.info("Cleaned up inactive user: {}", username);
        return true;
      }
      return false;
    });
  }
}
