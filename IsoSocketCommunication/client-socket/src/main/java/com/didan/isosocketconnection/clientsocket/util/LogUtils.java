package com.didan.isosocketconnection.clientsocket.util;

import com.didan.isosocketconnection.clientsocket.constant.ISO8385Message;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;

@UtilityClass
@Slf4j
public class LogUtils {

  public static void logISOPayload(String add, ISOMsg isoMsg) {
    StringBuilder msgLogger = new StringBuilder("\n");

    for (int i = 0; i <= isoMsg.getMaxField(); i++) { // Duyet tat ca cac field cua ISOMsg
      if (isoMsg.hasField(i) && i != ISO8385Message.PASSWORD_CURRENT) { // Neu field do co du lieu va khong phai field Password
        msgLogger.append("    Field-").append(i).append(" : ").append(isoMsg.getString(i)).append("\n"); // Them field do vao msgLogger
      }
    }
    log.info("{} Transfer ISO payload:{}", add, msgLogger); // In ra log
  }

  public static void logISOResponse(ISOMsg isoMsg) {
    StringBuilder msgLogger = new StringBuilder("");

    for (int i = 1; i <= isoMsg.getMaxField(); i++) {
      if (isoMsg.hasField(i)) {
        msgLogger.append("    Field-" + i + " : "
            + isoMsg.getString(i));
      }
    }
    log.info("Transfer ISO response:{}", msgLogger);
  }
}
