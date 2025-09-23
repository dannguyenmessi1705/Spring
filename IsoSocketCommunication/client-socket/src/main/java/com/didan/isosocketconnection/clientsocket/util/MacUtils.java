package com.didan.isosocketconnection.clientsocket.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MacUtils {

  @Value("${netty.socket.connection.mac-key}")
  private String secretKey;

  private static final GenericPackager packager = ConnectorUtils.readGenericManager("isodef.xml");
  private static int[] fieldSign = {0, 2, 3, 4, 5, 6, 7, 11, 32, 37, 38, 39, 41, 42, 48, 63, 66, 90, 102, 103, 120};
  private static final String ALGORITHM = "HmacSHA256";

  public byte[] encrypt(String data) throws NoSuchAlgorithmException, InvalidKeyException {
    byte[] secretKeyBytes = secretKey.getBytes(); // Chuyen doi chuoi secretKey thanh mang byte
    SecretKeySpec spec = new SecretKeySpec(secretKeyBytes, ALGORITHM); // Tao doi tuong SecretKeySpec tu mang byte va thuat toan HMAC-SHA256
    Mac mac = Mac.getInstance(ALGORITHM); // Tao doi tuong Mac voi thuat toan HMAC-SHA256
    mac.init(spec); // Khoi tao doi tuong Mac voi khoa bi mat
    return mac.doFinal(data.getBytes()); // Thuc hien phep toan HMAC tren du lieu va tra ve ket qua
  }

  public byte[] decrypt(String data) throws NoSuchAlgorithmException, InvalidKeyException {
    byte[] secretKeyBytes = secretKey.getBytes(); // Chuyen doi chuoi secretKey thanh mang byte
    SecretKeySpec spec = new SecretKeySpec(secretKeyBytes, ALGORITHM); // Tao doi tuong SecretKeySpec tu mang byte va thuat toan HMAC-SHA256
    Mac receiveMac = Mac.getInstance(ALGORITHM); // Tao doi tuong Mac voi thuat toan HMAC-SHA256
    receiveMac.init(spec); // Khoi tao doi tuong Mac voi khoa bi mat
    return receiveMac.doFinal(data.getBytes()); // Thuc hien phep toan HMAC tren du lieu va tra ve ket qua
  }

  public String generateMac(ISOMsg isoMsg) {
    try {
      StringBuilder rawData = new StringBuilder(); // Chuoi de luu tru du lieu can ky so
      for (int field : fieldSign) { // Vong lap qua tung field trong mang fieldSign
        if (isoMsg.hasField(field)) { // Kiem tra neu isoMsg co field hien tai
          String isoFieldClass = packager.getFieldPackager(field).getClass().getName(); // Lay ten lop cua field hien tai
          String fieldValue = (String) isoMsg.getValue(field); // Lay gia tri cua field hien tai
          if (isoFieldClass.contains("LLL")) {
            rawData.append(ISOUtil.padleft(String.valueOf(fieldValue.length()), 3, '0')).append(fieldValue); // Neu field la LLL (co do dai 3 chu so) thi them do dai va gia tri vao rawData
          } else if (isoFieldClass.contains("LL")) {
            rawData.append(ISOUtil.padleft(String.valueOf(fieldValue.length()), 2, '0')).append(fieldValue); // Neu field la LL (co do dai 2 chu so) thi them do dai va gia tri vao rawData
          } else {
            if (field == 4) {
              rawData.append(ISOUtil.padleft(fieldValue, 12, '0')); // Neu field la 4 (so tien) thi them gia tri da duoc can bang 0 vao rawData
            } else {
              rawData.append(fieldValue); // Neu khong phai cac truong hop tren thi chi them gia tri vao rawData
            }
          }
        }
      }
      return ISOUtil.hexString(encrypt(rawData.toString())).toUpperCase(); // Tra ve chuoi hex cua du lieu da duoc ky so
    } catch (Exception ex) {
      log.error("MacUtils.generateMac error: " + ex.getMessage(), ex);
      return "ERROR";
    }
  }

  public String compareMac(ISOMsg isoMsg, String responseMac) {
    try {
      String generatedMac = generateMac(isoMsg); // Sinh ra MAC tu isoMsg
      String responseMacString = ISOUtil.hexString(decrypt(responseMac)).toUpperCase(); // Giai ma MAC tu responseMac va chuyen doi sang chuoi hex
      if (generatedMac.equals(responseMacString)) {
        return "Message authentication successful"; // Neu giong nhau tra ve thong bao thanh cong
      } else {
        return "Message authentication failed"; // Neu khong giong nhau tra ve thong bao that bai
      }
    } catch (Exception ex) {
      log.error("MacUtils.compareMac error: " + ex.getMessage(), ex);
      return "99"; // Trong truong hop co loi tra ve "99"
    }
  }
}
