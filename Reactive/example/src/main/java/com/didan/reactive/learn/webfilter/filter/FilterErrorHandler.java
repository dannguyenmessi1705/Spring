package com.didan.reactive.learn.webfilter.filter;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FilterErrorHandler {

  private final ServerCodecConfigurer codecConfigurer; // Cấu hình codec để xử lý mã hóa và giải mã dữ liệu
  private ServerResponse.Context context; // Ngữ cảnh để xử lý phản hồi máy chủ

  @PostConstruct
  private void init() {
    this.context = new ContextImpl(codecConfigurer); // Khởi tạo ngữ cảnh với cấu hình codec ngay sau khi bean được tạo
  }

  // Class nội bộ để triển khai ServerResponse.Context
  private record ContextImpl(ServerCodecConfigurer codecConfigurer) implements ServerResponse.Context {

    // Ghi đè phương thức để trả về danh sách các bộ ghi thông điệp HTTP
    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
      return this.codecConfigurer.getWriters(); // Trả về danh sách các bộ ghi thông điệp HTTP từ cấu hình codec
    }

    // Ghi đè phương thức để trả về danh sách các bộ phân giải view
    @Override
    public List<ViewResolver> viewResolvers() {
      return List.of();
    }
  }

  /**
   * Gửi phản hồi chi tiết về vấn đề qua class Problem Detail với mã trạng thái HTTP và thông điệp cụ thể
   *
   * @param exchange
   * @param httpStatus
   * @param message
   * @return
   */
  public Mono<Void> sendProblemDetail(ServerWebExchange exchange, HttpStatus httpStatus, String message) {
    var problem = ProblemDetail.forStatusAndDetail(httpStatus, message); // Tạo đối tượng ProblemDetail với mã trạng thái và thông điệp chi tiết
    return ServerResponse
        .status(httpStatus) // Thiết lập mã trạng thái cho phản hồi
        .bodyValue(problem) // Đặt đối tượng ProblemDetail làm nội dung phản hồi
        .flatMap(sr -> sr.writeTo(exchange, this.context)); // Ghi phản hồi vào exchange sử dụng ngữ cảnh đã tạo
  }
}
