package com.didan.testperformance.first.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {

  public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
  public static final String YY_MM_DD_HH_MM_SS = "yyyy-MM-dd - HH:mm:ss Z";

  public String dateToString(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(date);
  }

  public String dateToString(Date date, String timeZone, String format) {
    // Creating a DateFormat class object to
    // convert the localtime to GMT
    DateFormat s = new SimpleDateFormat(format);
    //  function will helps to get the GMT Timezone
    // using the getTimeZOne() method
    s.setTimeZone(TimeZone.getTimeZone(timeZone));
    return s.format(date);
  }
}
