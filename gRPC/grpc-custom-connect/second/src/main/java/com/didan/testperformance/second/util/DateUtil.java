package com.didan.testperformance.second.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {
  public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
  public static final String YY_MM_DD_HH_MM_SS = "yyyy-MM-dd - HH:mm:ss Z";

  public String dateToString(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(date);
  }
}
