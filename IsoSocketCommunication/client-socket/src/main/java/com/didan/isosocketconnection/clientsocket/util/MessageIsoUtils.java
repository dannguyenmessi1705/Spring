package com.didan.isosocketconnection.clientsocket.util;

import lombok.experimental.UtilityClass;
import org.jpos.iso.ISOFieldValidator;
import org.jpos.iso.IVA_ALPHANUM;
import org.jpos.iso.IVA_NUM;

@UtilityClass
public class MessageIsoUtils {

  /**
   * Lay mang cac ISOFieldValidator cho cac field thong dung trong thong diep ISO 8583
   * @return
   */
  public static ISOFieldValidator[] getIsoFieldValidators() {
    ISOFieldValidator mti = new IVA_NUM(true, 4, 4, "MTI"); // MTI la bat buoc, do dai 4 ky tu so, khong duoc rong, mo ta la "MTI"
    mti.setFieldId(0); // Dat ID cho field MTI la 0
    ISOFieldValidator f2 = new IVA_NUM(true, 19, "PAN - PRIMARY ACCOUNT NUMBER"); // Field 2 la PAN, bat buoc, do dai toi da 19 ky tu so, mo ta la "PAN - PRIMARY ACCOUNT NUMBER"
    f2.setFieldId(2); // Dat ID cho field 2 la 2
    ISOFieldValidator f3 = new IVA_NUM(true, 6, 6, "PROCESSING CODE");
    f3.setFieldId(3);
    ISOFieldValidator f4 = new IVA_NUM(true, 12, 12, "AMOUNT, TRANSACTION");
    f4.setFieldId(4);
    ISOFieldValidator f5 = new IVA_NUM(true, 12, 12, "AMOUNT, SETTLEMENT");
    f5.setFieldId(5);
    ISOFieldValidator f7 = new IVA_NUM(true, 10, 10, "TRANSMISSION DATE AND TIME");
    f7.setFieldId(7);
    ISOFieldValidator f9 = new IVA_NUM(true, 8, 8, "CONVERSION RATE, SETTLEMENT");
    f9.setFieldId(9);
    ISOFieldValidator f11 = new IVA_NUM(true, 6, 6, "SYSTEM TRACE AUDIT NUMBER");
    f11.setFieldId(11);
    ISOFieldValidator f12 = new IVA_NUM(true, 6, 6, "TIME, LOCAL TRANSACTION");
    f12.setFieldId(12);
    ISOFieldValidator f13 = new IVA_NUM(true, 4, 4, "DATE, LOCAL TRANSACTION");
    f13.setFieldId(13);
    ISOFieldValidator f15 = new IVA_NUM(true, 4, 4, "DATE, SETTLEMENT");
    f15.setFieldId(15);
    ISOFieldValidator f32 = new IVA_NUM(true, 11, "ACQUIRING INSTITUTION IDENT CODE");
    f32.setFieldId(32);
    ISOFieldValidator f37 = new IVA_ALPHANUM(true, 12, 12, "RETRIEVAL REFERENCE NUMBER");
    f37.setFieldId(37);
    ISOFieldValidator f38 = new IVA_ALPHANUM(true, 6, 6, "AUTHORIZATION IDENTIFICATION RESPONSE");
    f38.setFieldId(38);
    ISOFieldValidator f39 = new IVA_ALPHANUM(true, 2, 2, "RESPONSE CODE");
    f39.setFieldId(39);
    ISOFieldValidator f41 = new IVA_ALPHANUM(true, 8, 8, "CARD ACCEPTOR TERMINAL IDENTIFICACION");
    f41.setFieldId(41);
    ISOFieldValidator f48 = new ISOFieldValidator(true, 999, "SENDER NAME");
    f48.setFieldId(48);
    ISOFieldValidator f49 = new IVA_ALPHANUM(true, 3, 3, "CURRENCY CODE, TRANSACTION");
    f49.setFieldId(49);
    ISOFieldValidator f50 = new IVA_ALPHANUM(true, 3, "CURRENCY CODE, SETTLEMENT");
    f50.setFieldId(50);
    ISOFieldValidator f60 = new IVA_ALPHANUM(true, 60, "User Defined Field");
    f60.setFieldId(60);
    ISOFieldValidator f62 = new IVA_ALPHANUM(true, 10, "Service Code");
    f62.setFieldId(62);
    ISOFieldValidator f63 = new IVA_ALPHANUM(true, 16, "Transaction Reference Number");
    f63.setFieldId(63);
    ISOFieldValidator f100 = new IVA_NUM(true, 11, "RECEIVING INSTITUTION IDENT CODE");
    f100.setFieldId(100);
    ISOFieldValidator f102 = new IVA_ALPHANUM(true, 28, "From ACCOUNT IDENTIFICATION");
    f102.setFieldId(102);
    ISOFieldValidator f103 = new IVA_ALPHANUM(true, 28, "To ACCOUNT IDENTIFICATION");
    f103.setFieldId(103);
    ISOFieldValidator f104 = new IVA_ALPHANUM(true, 210, "TRANSACTION DESCRIPTION");
    f104.setFieldId(104);
    ISOFieldValidator f128 = new IVA_ALPHANUM(true, 16, 16, "Message Authentication Code");
    f128.setFieldId(128);
    return new ISOFieldValidator[]{
        mti,
        f2,
        f3,
        f4,
        f5,
        f7,
        f9,
        f11,
        f12,
        f13,
        f15,
        f32,
        f37,
        f38,
        f39,
        f41,
        f48,
        f49,
        f50,
        f60,
        f62,
        f63,
        f100,
        f102,
        f103,
        f104,
        f128
    };
  }
}
