package com.didan.springwebrtc.storage;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@UtilityClass
public class LocalStorage {

  public static Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
  // Lưu trữ người dùng và phòng của họ
  public static Map<String, String> userRooms = new ConcurrentHashMap<>();
  // Lưu trữ các kết nối WebRTC
  public static Map<String, LocalDateTime> userConnections = new ConcurrentHashMap<>();
}
