package com.didan.isosocketconnection.serversocket.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

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
