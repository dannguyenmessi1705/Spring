package com.didan.pattern.microservices_sequence.orchestrator_parallel.service;

import com.didan.pattern.microservices_sequence.orchestrator_parallel.dto.OrchestrationRequestContext;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

  // Inject tất cả các orchestrator_parallel đã implement Orchestrator interface
  private final List<Orchestrator> orchestrators;

  private Sinks.Many<OrchestrationRequestContext> sink; // Tạo một Sinks.Many để phát các sự kiện hủy đơn hàng
  private Flux<OrchestrationRequestContext> flux; // Tạo một Flux để lắng nghe các sự kiện từ Sinks.Many

  @PostConstruct
  public void init() {
    this.sink = Sinks
        .many() // Tạo Sinks.Many để phát các sự kiện hủy đơn hàng
        .multicast() // Cho phép nhiều người đăng ký lắng nghe các sự kiện từ sink (multicast - nhiều người nghe, unicast - một người nghe, replay - phát lại, v.v)
        .onBackpressureBuffer(); // Sử dụng bộ đệm để xử lý áp lực ngược (backpressure) nếu có nhiều sự kiện được phát ra cùng lúc

    this.flux = this.sink.asFlux().publishOn(Schedulers.boundedElastic()); // Chuyển đổi Sinks.Many thành Flux và sử dụng boundedElastic scheduler để xử lý các sự kiện một cách linh hoạt

    orchestrators.forEach(o -> this.flux.subscribe(o.cancel())); // Đăng ký mỗi orchestrator_parallel để lắng nghe các sự kiện hủy đơn hàng từ flux
  }

  /**
   * Sự kiện hủy đơn hàng, sẽ phát đến tất cả các orchestrator_parallel đã đăng ký
   * @param ctx
   */
  public void cancelOrder(OrchestrationRequestContext ctx) {
    this.sink.tryEmitNext(ctx); // Phát sự kiện hủy đơn hàng mới đến tất cả các orchestrator_parallel đã đăng ký
  }
}

  /**
   * Backpressure là một khái niệm trong lập trình phản ứng (reactive programming) dùng để mô tả tình huống khi một nguồn dữ liệu (publisher) phát ra dữ liệu nhanh hơn so với khả năng xử lý của người tiêu thụ dữ liệu (subscriber). Khi điều này xảy ra, có thể dẫn đến việc mất dữ liệu hoặc quá tải hệ thống. Để giải quyết vấn đề này, các framework lập trình phản ứng như Reactor cung cấp các cơ chế quản lý áp lực ngược (backpressure management) nhằm điều chỉnh tốc độ phát dữ liệu giữa publisher và subscriber.
   * Scheduler boundedElastic trong Reactor là một loại scheduler được thiết kế để xử lý các tác vụ có thể chặn (blocking tasks) một cách hiệu quả. Scheduler này sử dụng một nhóm luồng (thread pool) có kích thước linh hoạt, cho phép tạo thêm luồng mới khi cần thiết, nhưng giới hạn tổng số luồng để tránh quá tải hệ thống. BoundedElastic scheduler thích hợp cho các tác vụ I/O hoặc các tác vụ dài hạn mà không nên chạy trên luồng chính (main thread) hoặc các luồng sự kiện (event loop threads).
   */