package com.didan.reactive.learn.updownstream.config;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {
  @Bean
  public Sinks.Many<ProductDto> sink() {
    return Sinks.many() // Tạo một Sink nhiều người nhận
        .replay() // Lưu lại tất cả các phần tử đã phát để phát lại cho người nhận mới
        .limit(1); // Giới hạn số phần tử lưu lại là 1 phần tử gần nhất
  }
}
