package com.didan.isosocketconnection.serversocket.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;

@UtilityClass
@Slf4j
public class LogUtils {

  /**
   * Log chi tiet payload cua ISOMsg
   * @param add
   * @param isoMsg
   */
  public static void logISOPayload(String add, ISOMsg isoMsg) {
    StringBuilder msgLogger = new StringBuilder("\n");

    for (int i = 0; i <= isoMsg.getMaxField(); i++) { // Duyet tat ca cac field cua ISOMsg
      if (isoMsg.hasField(i) && i != com.didan.isosocketconnection.serversocket.constant.ISO8385Message.PASSWORD_CURRENT) { // Neu field do co du lieu va khong phai field Password
        msgLogger.append("    Field-").append(i).append(" : ").append(isoMsg.getString(i)).append("\n"); // Them field do vao msgLogger
      }
    }
    log.info("{} Transfer ISO payload:{}", add, msgLogger); // In ra log
  }

  /**
   * Log chi tiet response cua ISOMsg
   * @param isoMsg
   */
  public static void logISOResponse(ISOMsg isoMsg) {
    StringBuilder msgLogger = new StringBuilder("");

    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
      if (isoMsg.hasField(i)) {
        msgLogger.append("    Field-").append(i).append(" : ").append(isoMsg.getString(i));
      }
    }
    log.info("Transfer ISO response:{}", msgLogger);
  }
}
