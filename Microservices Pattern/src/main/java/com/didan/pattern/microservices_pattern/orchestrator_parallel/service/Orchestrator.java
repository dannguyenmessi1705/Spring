package com.didan.pattern.microservices_pattern.orchestrator_parallel.service;

import com.didan.pattern.microservices_pattern.orchestrator_parallel.dto.OrchestrationRequestContext;
import java.util.function.Consumer;
import java.util.function.Predicate;
import reactor.core.publisher.Mono;

public abstract class Orchestrator {

  /**
   * Hàm tạo request để gọi service
   * @param ctx
   * @return
   */
  public abstract Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx);

  /**
   * Hàm kiểm tra kết quả trả về từ service có thành công hay không
   * @return
   */
  public abstract Predicate<OrchestrationRequestContext> isSuccess();

  /**
   * Hàm hủy bỏ thao tác đã thực hiện trên service
   * @return
   */
  public abstract Consumer<OrchestrationRequestContext> cancel();
}
