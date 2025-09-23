package com.didan.springwebrtc.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(WebRTCException.class)
  public ResponseEntity<Map<String, Object>> handleWebRTCException(WebRTCException ex) {
    log.error("WebRTC error: {} - {}", ex.getCode(), ex.getMessage());

    Map<String, Object> error = Map.of(
        "error", ex.getCode(),
        "message", ex.getMessage(),
        "timestamp", LocalDateTime.now()
    );

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    Map<String, Object> error = Map.of(
        "error", "INTERNAL_ERROR",
        "message", "An unexpected error occurred",
        "timestamp", LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
