package com.didan.pattern.microservices_pattern.orchestrator_sequence.service;

import com.didan.pattern.microservices_pattern.orchestrator_sequence.dto.OrchestrationRequestContext;
import com.didan.pattern.microservices_pattern.orchestrator_sequence.exception.OrderFulfillmentFailure;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

public abstract class Orchestrator {

  /**
   * Hàm tạo request để gọi service
   *
   * @param ctx
   * @return
   */
  public abstract Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx);

  /**
   * Hàm kiểm tra kết quả trả về từ service có thành công hay không
   *
   * @return
   */
  public abstract Predicate<OrchestrationRequestContext> isSuccess();

  /**
   * Hàm hủy bỏ thao tác đã thực hiện trên service
   *
   * @return
   */
  public abstract Consumer<OrchestrationRequestContext> cancel();

  /**
   * Hàm xử lý trạng thái trả về từ service
   * @return
   */
  protected BiConsumer<OrchestrationRequestContext, SynchronousSink<OrchestrationRequestContext>> statusHandler() {
    return (ctx, sink) -> {
      if (isSuccess().test(ctx)) { // Kiểm tra trạng thái thành công
        sink.next(ctx); // Thực hiện điều kiện thành công (tiếp tục quy trình)
      } else {
        sink.error(new OrderFulfillmentFailure()); // Ném lỗi để kích hoạt quá trình hủy bỏ (rollback)
      }
    };
  }
}
