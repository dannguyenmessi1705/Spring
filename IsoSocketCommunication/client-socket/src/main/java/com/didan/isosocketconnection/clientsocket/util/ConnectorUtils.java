package com.didan.isosocketconnection.clientsocket.util;

import java.io.InputStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOFieldValidator;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.GenericValidatingPackager;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@UtilityClass
public class ConnectorUtils {

  /**
   * Doc file cau hinh GenericPackager tu duong dan isoPath trong classpath
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
}
