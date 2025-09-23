package com.didan.isosocketconnection.serversocket.util;

import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

@Slf4j
public class MsgUtils {

  private GenericPackager isoPackage;



  public MsgUtils() {
    try {
      this.isoPackage = new GenericPackager();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  public GenericPackager getIsoPackage() {
    return isoPackage;
  }

  /**
   * In ra noi dung ISOMsg
   * @param iso
   * @param target
   */
  public void print(ISOMsg iso, String target) {
    try {
      if (iso.isRequest()) { // Neu la request
        log.info("***************REQUEST TO {}***************", target);
      }
      if (iso.isResponse()) { // Neu la response
        log.info("****************RESPONSE FROM {}***************", target);
      }
      LogUtils.logISOPayload(target, iso); // In ra noi dung ISOMsg
      log.info("*********************************************");
    } catch (Exception ex) {
      log.info(ex.getMessage(), ex);
    }
  }

  public MsgUtils(GenericPackager isoPackage) {
    log.info("init MsgUtils: isoPackage {}", isoPackage);
    this.isoPackage = isoPackage;
  }

  /**
   * In ra noi dung ISOMsg
   * @param data
   * @return
   * @throws ISOException
   */
  public ISOMsg unpack(byte[] data) throws ISOException {
    ISOMsg isoMsg = new ISOMsg(); // Tao moi 1 ISOMsg
    isoMsg.setPackager(isoPackage); // Gan packager
    isoMsg.unpack(data); // Unpack du lieu
    return isoMsg; // Tra ve ISOMsg
  }

  /**
   * Pack du lieu ISOMsg (Client va Server deu su dung ham nay de pack du lieu)
   * @param isoMsg
   * @return
   * @throws ISOException
   */
  public byte[] pack(ISOMsg isoMsg) throws ISOException {
    ISOMsg obj = (ISOMsg) isoMsg.clone(); // Tao moi 1 ISOMsg de khong bi anh huong boi doi tuong truyen vao
    obj.setPackager(isoPackage); // Gan packager
    byte[] arrByteRequest = obj.pack(); // Pack du lieu

    int l = arrByteRequest.length; // Do dai du lieu
    byte[] msg = new byte[4 + l]; // Tao mang moi de chua do dai + du lieu
    msg[0] = ((byte) (l / 1000 + 48)); // Lay hang nghin
    msg[1] = ((byte) (l % 1000 / 100 + 48)); // Lay hang tram
    msg[2] = ((byte) (l % 100 / 10 + 48)); // Lay hang chuc
    msg[3] = ((byte) (l % 10 + 48)); // Lay hang don vi
    System.arraycopy(arrByteRequest, 0, msg, 4, arrByteRequest.length); // Copy du lieu vao mang moi
    return msg; // Tra ve mang moi
  }

}
