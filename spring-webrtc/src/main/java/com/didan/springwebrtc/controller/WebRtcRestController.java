package com.didan.springwebrtc.controller;

import com.didan.springwebrtc.service.WebRtcService;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@RestController
@RequestMapping("/api/webrtc")
@RequiredArgsConstructor
public class WebRtcRestController {

  private final WebRtcService webRtcService;

  @GetMapping("/rooms/{roomId}/stats")
  public ResponseEntity<Map<String, Object>> getRoomStats(@PathVariable String roomId) {
    Map<String, Object> stats = webRtcService.getRoomStats(roomId);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/rooms/{roomId}/users")
  public ResponseEntity<Set<String>> getRoomUsers(@PathVariable String roomId) {
    Set<String> users = webRtcService.getUsersInRoom(roomId);
    return ResponseEntity.ok(users);
  }

  @PostMapping("/rooms/{roomId}/validate")
  public ResponseEntity<Map<String, Object>> validateRoom(@PathVariable String roomId) {
    Set<String> users = webRtcService.getUsersInRoom(roomId);
    boolean canJoin = users.size() < 10; // Example limit, adjust as needed

    return ResponseEntity.ok(Map.of(
        "canJoin", canJoin,
        "currentUsers", users.size(),
        "maxUsers", 10 // Example limit, adjust as needed
    ));

  }
}
