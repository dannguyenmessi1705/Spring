package com.didan.reactive.learn.comparemvc.controller;

import com.didan.reactive.learn.comparemvc.dto.ExampleUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("reactive")
@Slf4j
public class ReactiveWeb {
  private final WebClient webClient = WebClient.builder()
      .baseUrl("https://6698c0eb2069c438cd6fd23e.mockapi.io")
      .build();

  @GetMapping("/users")
  public Flux<ExampleUserDto> getUsers() {
    return webClient.get()
        .uri("/api/v1/users")
        .retrieve() // Gửi yêu cầu GET và lấy phản hồi
        .bodyToFlux(ExampleUserDto.class) // Sử dụng bodyToFlux để nhận dữ liệu dạng Flux
        .onErrorComplete() // Nếu có lỗi xảy ra, hoàn thành Flux mà không phát sinh lỗi
        .doOnNext(user -> log.info("Retrieved user: {}", user)); // Mỗi khi nhận được một user, ghi log thông tin user đó
  }

  @GetMapping(value = "/users/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // Sử dụng MediaType.TEXT_EVENT_STREAM_VALUE để hỗ trợ streaming dữ liệu
  public Flux<ExampleUserDto> getUserStream() {
    return webClient.get()
        .uri("/api/v1/users")
        .retrieve()
        .bodyToFlux(ExampleUserDto.class)
        .doOnNext(user -> log.info("Streamed user: {}", user));
  }
}
