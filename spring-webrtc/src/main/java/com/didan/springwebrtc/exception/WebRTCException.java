package com.didan.springwebrtc.exception;

import lombok.Getter;

/**
 * @author dannd1
 * @since 7/11/2025
 */
@Getter
public class WebRTCException extends RuntimeException {

  private final String code;

  public WebRTCException(String code, String message) {
    super(message);
    this.code = code;
  }

  public WebRTCException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}
