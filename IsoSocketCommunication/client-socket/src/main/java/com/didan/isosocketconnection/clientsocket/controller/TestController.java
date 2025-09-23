package com.didan.isosocketconnection.clientsocket.controller;

import com.didan.isosocketconnection.clientsocket.service.IsoService;
import com.didan.isosocketconnection.clientsocket.util.DataUtils;
import com.didan.isosocketconnection.clientsocket.util.DateUtils;
import com.didan.isosocketconnection.clientsocket.util.MacUtils;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
  private final IsoService isoService;
  private final MacUtils macUtils;

  @GetMapping("/test")
  public ResponseEntity<?> test() throws ISOException {
    Date transDate = new Date();
    ISOMsg isoMsg = new ISOMsg();
    isoMsg.setMTI("0200");
    isoMsg.set(2, "123456789");
    isoMsg.set(3, "432020");
    isoMsg.set(4, "0");
    isoMsg.set(7, DateUtils.dateToString(transDate, "GMT", "MMddHHmmss"));
    isoMsg.set(11, "170502");
    isoMsg.set(12, DateUtils.dateToString(transDate, "GMT+7", "HHmmss"));
    isoMsg.set(13, DateUtils.dateToString(transDate, "GMT+7", "MMdd"));
    isoMsg.set(22, "000");
    isoMsg.set(25, "00");
    isoMsg.set(32, "970422");
    isoMsg.set(37, DataUtils.genField37(isoMsg, new Date()));
    isoMsg.set(41, "00000001");
    isoMsg.set(42, "000000000000001");
    isoMsg.set(43, DataUtils.genF43());
    isoMsg.set(48, "NGUYEN DI DAN");
    isoMsg.set(49, "704");
    isoMsg.set(60, "05");
    isoMsg.set(62, "IF_INQ");
    if ("432020".equals(isoMsg.getString(3))) {
      isoMsg.set(100, "970405");
    }
    isoMsg.set(102, "123456789");
    isoMsg.set(103, "000000123456789");
    isoMsg.set(104, "TEST SOCKET");
    isoMsg.set(128, macUtils.generateMac(isoMsg));
    ISOMsg response = isoService.getISOMSgFromAPI(isoMsg);
    String res = response.getString("39");
    log.info(response.toString());
    log.info("Field 39: {}", res);
    String name = response.getString(120);
    return ResponseEntity.ok(name);
  }
}
