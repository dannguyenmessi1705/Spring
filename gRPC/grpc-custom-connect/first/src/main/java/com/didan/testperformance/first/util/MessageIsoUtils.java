package com.didan.testperformance.first.util;

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
    mti.setFieldId(0);
    ISOFieldValidator f104 = new IVA_ALPHANUM(true, 999, "MESSAGE");
    f104.setFieldId(104);
    return new ISOFieldValidator[]{
        mti,
        f104
    };
  }
}
