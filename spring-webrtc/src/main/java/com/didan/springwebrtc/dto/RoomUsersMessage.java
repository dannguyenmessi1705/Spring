package com.didan.springwebrtc.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomUsersMessage {

  private String roomId;
  private Set<String> users;
}
