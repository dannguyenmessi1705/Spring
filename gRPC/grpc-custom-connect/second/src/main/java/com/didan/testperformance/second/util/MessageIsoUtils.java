package com.didan.testperformance.second.util;

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
    ISOFieldValidator f2 = new IVA_ALPHANUM(true, 19, "MESSAGE");
    f2.setFieldId(2);
    return new ISOFieldValidator[]{
        mti,
        f2
    };
  }
}
