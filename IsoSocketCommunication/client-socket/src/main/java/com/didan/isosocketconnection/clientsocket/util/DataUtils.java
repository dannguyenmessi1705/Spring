package com.didan.isosocketconnection.clientsocket.util;

import java.util.Calendar;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

@UtilityClass
public class DataUtils {

  public static String genField37(ISOMsg isoMsg, Date date) throws ISOException {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int doy = cal.get(Calendar.DAY_OF_YEAR);
    String yearStr = String.valueOf(year).substring(3, 4);
    String doyStr = ISOUtil.padleft(String.valueOf(doy), 3, '0');
    return yearStr + doyStr + isoMsg.getString(7).substring(4, 6) + isoMsg.getString(11);
  }

  public static String genF43() {
    try {
      String bankCodePad = ISOUtil.padright("DIDAN", 23, ' ');
      String mmPad = ISOUtil.padright(bankCodePad + "FC", 37, ' ');
      return mmPad + "VNM";
    } catch (Exception ex) {
      return "DIDAN FC VNM";
    }
  }
}
