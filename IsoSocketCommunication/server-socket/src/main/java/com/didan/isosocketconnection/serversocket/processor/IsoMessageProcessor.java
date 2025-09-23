package com.didan.isosocketconnection.serversocket.processor;

import com.didan.isosocketconnection.serversocket.util.DateUtils;
import com.didan.isosocketconnection.serversocket.util.LogUtils;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

/**
 * Service dùng để xử lý các tin nhắn ISO 8583. Nó nhận một tin nhắn yêu cầu, xử lý nó dựa trên loại tin nhắn
 */
@Slf4j
@Service
public class IsoMessageProcessor {

  /**
   * Xử lý tin nhắn ISO 8583
   * @param request
   * @return
   */
  public ISOMsg processMessage(ISOMsg request) {
    try {
      LogUtils.logISOPayload("SERVER-REQUEST", request); // Log request ISO message

      String mti = request.getMTI(); // Lấy MTI từ tin nhắn yêu cầu
      ISOMsg response = new ISOMsg(); // Tạo tin nhắn phản hồi

      // Process different message types
      switch (mti) { // Xử lý các loại tin nhắn khác nhau
        case "0200": // Financial transaction request
          response = processFinancialTransaction(request);
          break;
        case "0800": // Network management request
          response = processNetworkManagement(request);
          break;
        case "0810": // Network management request (ping)
          response = processPingRequest(request);
          break;
        default:
          response = createErrorResponse(request, "01"); // Function not supported
          break;
      }

      LogUtils.logISOPayload("SERVER-RESPONSE", response); // Log response ISO message
      return response; // Trả về tin nhắn phản hồi

    } catch (Exception e) {
      log.error("Error processing message", e);
      return createErrorResponse(request, "96"); // System error
    }
  }

  private ISOMsg processFinancialTransaction(ISOMsg request) throws Exception {
    ISOMsg response = new ISOMsg();
    response.setMTI("0210"); // Financial transaction response

    // Copy key fields from request
    copyCommonFields(request, response);

    // Process based on processing code
    String processingCode = request.getString(3);
    if ("432020".equals(processingCode)) {
      // Balance inquiry
      response.set(39, "00"); // Approved
      response.set(38, "00"); // Available balance
      response.set(120, "NGUYEN VAN A"); // Account holder name
    } else {
      // Default approval for other transactions
      response.set(39, "68"); // Approved
      response.set(38, "68"); // Authorization ID
    }

    return response;
  }

  private ISOMsg processNetworkManagement(ISOMsg request) throws Exception {
    ISOMsg response = new ISOMsg();
    response.setMTI("0810"); // Network management response

    copyCommonFields(request, response);
    response.set(39, "00"); // Approved

    return response;
  }

  private ISOMsg processPingRequest(ISOMsg request) throws Exception {
    ISOMsg response = new ISOMsg();
    response.setMTI("0810"); // Network management response

    copyCommonFields(request, response);
    response.set(39, "00"); // Approved

    return response;
  }

  private ISOMsg createErrorResponse(ISOMsg request, String responseCode) {
    try {
      ISOMsg response = new ISOMsg();
      String requestMti = request.getMTI();

      // Convert request MTI to response MTI
      if (requestMti.startsWith("02")) {
        response.setMTI("0210");
      } else if (requestMti.startsWith("08")) {
        response.setMTI("0810");
      } else {
        response.setMTI("0810");
      }

      copyCommonFields(request, response);
      response.set(39, responseCode);

      return response;
    } catch (Exception e) {
      log.error("Error creating error response", e);
      return null;
    }
  }

  private void copyCommonFields(ISOMsg request, ISOMsg response) throws Exception {
    Date now = new Date();

    // Copy fields that should be echoed back
    if (request.hasField(2)) {
      response.set(2, request.getString(2)); // PAN
    }
    if (request.hasField(3)) {
      response.set(3, request.getString(3)); // Processing code
    }
    if (request.hasField(4)) {
      response.set(4, request.getString(4)); // Amount
    }
    if (request.hasField(7)) {
      response.set(7, request.getString(7)); // Transmission date/time
    }
    if (request.hasField(11)) {
      response.set(11, request.getString(11)); // STAN
    }
    if (request.hasField(12)) {
      response.set(12, DateUtils.dateToString(now, "GMT+7", "HHmmss")); // Local time
    }
    if (request.hasField(13)) {
      response.set(13, DateUtils.dateToString(now, "GMT+7", "MMdd")); // Local date
    }
    if (request.hasField(22)) {
      response.set(22, request.getString(22)); // POS entry mode
    }
    if (request.hasField(25)) {
      response.set(25, request.getString(25)); // Service condition code
    }
    if (request.hasField(32)) {
      response.set(32, request.getString(32)); // Acquiring institution
    }
    if (request.hasField(37)) {
      response.set(37, request.getString(37)); // Reference number
    }
    if (request.hasField(41)) {
      response.set(41, request.getString(41)); // Terminal ID
    }
    if (request.hasField(42)) {
      response.set(42, request.getString(42)); // Merchant ID
    }
    if (request.hasField(43)) {
      response.set(43, request.getString(43)); // Card acceptor name
    }
    if (request.hasField(49)) {
      response.set(49, request.getString(49)); // Currency code
    }
    if (request.hasField(60)) {
      response.set(60, request.getString(60)); // Additional data
    }
    if (request.hasField(62)) {
      response.set(62, request.getString(62)); // Service code
    }
    if (request.hasField(100)) {
      response.set(100, request.getString(100)); // Receiving institution
    }
    if (request.hasField(102)) {
      response.set(102, request.getString(102)); // From account
    }
    if (request.hasField(103)) {
      response.set(103, request.getString(103)); // To account
    }
    if (request.hasField(104)) {
      response.set(104, request.getString(104)); // Transaction description
    }
  }
}
