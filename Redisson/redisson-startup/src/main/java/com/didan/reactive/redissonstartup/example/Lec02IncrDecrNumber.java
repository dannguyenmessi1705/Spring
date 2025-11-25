package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec02IncrDecrNumber {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void incrdecrNumber() throws InterruptedException {
    log.info("Starting Number Store");
    RAtomicLongReactive atmoic = redissonReactiveClient.getAtomicLong("user:1:visits"); // Lấy AtomicLong phản ứng cho khóa "user:1:visits"
    Mono<Void> mono = Flux.range(1, 5) // Tạo Flux từ 1 đến 30
        .delayElements(Duration.ofSeconds(1)) // Giãn cách mỗi phần tử 1 giây
        .flatMap(i -> atmoic.incrementAndGet()) // Tăng giá trị AtomicLong lên 1 và lấy giá trị mới và trả về Mono<Long>
        .doOnNext(i -> log.info("Incr: {}", i))  // In ra giá trị sau khi tăng
        .then();
    mono.subscribe(); // Thực thi chuỗi các thao tác tăng giá trị (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }
}
