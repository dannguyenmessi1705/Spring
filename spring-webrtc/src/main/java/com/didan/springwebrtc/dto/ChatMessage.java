package com.didan.springwebrtc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessage {

    private String type;
    private String from;
    private String to; // null for room messages, username for private messages
    private String content;
    private String roomId;
    private LocalDateTime timestamp;
    private boolean isPrivate;
}
