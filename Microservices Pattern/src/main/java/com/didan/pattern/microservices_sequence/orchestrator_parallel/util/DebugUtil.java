package com.didan.pattern.microservices_sequence.orchestrator_parallel.util;

import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.OrchestrationRequestContext;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@UtilityClass
public class DebugUtil {

  public static void print(OrchestrationRequestContext ctx) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctx));
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
