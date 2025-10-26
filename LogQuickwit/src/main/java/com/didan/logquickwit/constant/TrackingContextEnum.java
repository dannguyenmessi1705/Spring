package com.didan.logquickwit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrackingContextEnum {
  X_FORWARD_FOR("x-forwarded-for", "forwardIP"),
  X_REAL_IP("x-real-ip", "clientIP"),
  X_REQUEST_ID("x-request-id", "requestID"),
  X_CORRELATION_ID("X-Correlation-ID", "correlationID"),
  X_ORIGINAL_FORWARD_FOR("x-original-forwarded-for", "xOriginalForwardFor"),
  APP_VERSION("app-version", "appVersion"),
  TYPE_OS("type-os", "typeOS"),
  PROCESS("process-name", "processName"),
  MSISDN("MSISDN", "msisdn"),
  IMEI_TELE("imei-tele", "imeiTele"),
  IMEI_APP("imei", "imei"),
  TRACE_ID("traceId", "traceId"),
  SPAN_ID("spanId", "spanId"),
  CLIENT("client", "client"),
  DURATION("duration", "duration"),
  OS_VERSION("os-version", "osVersion"),
  USER_AGENT("user-agent", "userAgent"),
  ACCOUNT_ID("account-id", "accountId"),
  USER("user", "user"),
  LOG_ID("logId", "logId");

  private final String headerKey;
  private final String threadKey;
}
