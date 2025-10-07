package com.didan.testperformance.second.service;

import com.didan.testperformance.second.constant.ProtocolTypeEnum;
import com.didan.testperformance.second.constant.RequestTypeEnum;
import com.didan.testperformance.second.constant.StatusEnum;
import com.didan.testperformance.second.entity.LogsInfoEntity;
import com.didan.testperformance.second.repository.LogsInfoRepository;
import com.didan.testperformance.second.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service dùng để xử lý các tin nhắn ISO 8583. Nó nhận một tin nhắn yêu cầu, xử lý nó dựa trên loại tin nhắn
 */
@Slf4j
public class IsoMessageProcessor {

  private final LogsInfoRepository logsInfoRepository;

  public IsoMessageProcessor(LogsInfoRepository logsInfoRepository) {
    this.logsInfoRepository = logsInfoRepository;
  }

  /**
   * Xử lý tin nhắn ISO 8583
   *
   * @param request
   * @return
   */
  public ISOMsg processMessage(ISOMsg request) {
    try {
      LogUtils.logISOPayload("SERVER-REQUEST", request); // Log request ISO message

      String mti = request.getMTI(); // Lấy MTI từ tin nhắn yêu cầu
      ISOMsg response; // Tạo tin nhắn phản hồi

      // Process different message types
      switch (mti) { // Xử lý các loại tin nhắn khác nhau
        case "0200": // Financial transaction request
          response = processFinancialTransaction(request);
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

  private ISOMsg processFinancialTransaction(ISOMsg request) throws ISOException {
    ISOMsg response = new ISOMsg();
    response.setMTI("0210"); // Financial transaction response

    // Copy key fields from request
    copyCommonFields(request, response);

    response.set(104, StatusEnum.SUCCESS.toString());

    LogsInfoEntity logsInfo = new LogsInfoEntity();
    logsInfo.setRequestId(request.getString(37));
    logsInfo.setMessage(request.getString(104));
    logsInfo.setRequestType(RequestTypeEnum.RECEIVE);
    logsInfo.setType(ProtocolTypeEnum.SOCKET);
    logsInfoRepository.saveAndFlush(logsInfo);

    return response;
  }

  private ISOMsg processPingRequest(ISOMsg request) throws ISOException {
    ISOMsg response = new ISOMsg();
    response.setMTI("0810"); // Network management response

    copyCommonFields(request, response);

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

      return response;
    } catch (Exception e) {
      log.error("Error creating error response", e);
      return null;
    }
  }

  private void copyCommonFields(ISOMsg request, ISOMsg response) {
    if (request.hasField(7)) {
      response.set(7, request.getString(7)); // Transmission date/time
    }
    if (request.hasField(11)) {
      response.set(11, request.getString(11)); // STAN
    }
    if (request.hasField(32)) {
      response.set(32, request.getString(32)); // Acquiring institution
    }
    if (request.hasField(37)) {
      response.set(37, request.getString(37)); // Reference number
    }
  }
}
