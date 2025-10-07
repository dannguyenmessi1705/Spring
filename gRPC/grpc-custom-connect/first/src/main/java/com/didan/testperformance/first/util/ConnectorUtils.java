package com.didan.testperformance.first.util;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldValidator;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.GenericValidatingPackager;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@UtilityClass
public class ConnectorUtils {

  /**
   * Doc file cau hinh GenericPackager tu duong dan isoPath trong classpath
   *
   * @param isoPath
   * @return
   */
  public GenericPackager readGenericManager(String isoPath) {
    try (InputStream packagerStream = new ClassPathResource(isoPath).getInputStream()) { // Doc file tu classpath
      return new GenericPackager(packagerStream); // Khoi tao GenericPackager
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex); // Log loi neu co
    }
    return null;
  }

  /**
   * Doc file cau hinh GenericValidatingPackager tu duong dan isoPath trong classpath
   *
   * @param isoPath
   * @return
   */
  public GenericValidatingPackager readGenericValidatingManager(String isoPath) {
    try (InputStream packagerStream = new ClassPathResource(isoPath).getInputStream()) { // Doc file tu classpath
      GenericValidatingPackager genericValidatingPackager = new GenericValidatingPackager(packagerStream); // Khoi tao GenericValidatingPackager
      ISOFieldValidator[] isoFieldValidators = MessageIsoUtils.getIsoFieldValidators();
      genericValidatingPackager.setFieldValidator(isoFieldValidators); // Thiet lap validator cho cac field ISO
      return genericValidatingPackager;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex); // Log loi neu co
    }
    return null;
  }

  public static String genField37(ISOMsg isoMsg, Date date) throws ISOException {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    int doy = cal.get(Calendar.DAY_OF_YEAR);
    String yearStr = String.valueOf(year).substring(3, 4);
    String doyStr = ISOUtil.padleft(String.valueOf(doy), 3, '0');
    return yearStr + doyStr + isoMsg.getString(7).substring(4, 6) + isoMsg.getString(11);
  }
}
