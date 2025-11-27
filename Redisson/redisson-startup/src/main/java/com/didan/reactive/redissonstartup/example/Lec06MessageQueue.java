package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class Lec06MessageQueue {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;
  private RBlockingDequeReactive<Long> blockingDequeReactive;

  public Lec06MessageQueue(RedissonClient redissonClient, RedissonReactiveClient redissonReactiveClient) {
    this.redissonClient = redissonClient;
    this.redissonReactiveClient = redissonReactiveClient;
    this.blockingDequeReactive = redissonReactiveClient.getBlockingDeque("message-queue", LongCodec.INSTANCE);
  }

  @PostConstruct
  public void consumer1() throws InterruptedException {
    log.info("Starting message queue in Consumer1");
    this.blockingDequeReactive.takeElements() // Lấy các phần tử từ hàng đợi theo cơ chế LIFO
        .doOnNext(i -> log.info("Consumer 1 received message: {}", i))  // Xử lý mỗi phần tử nhận được
        .doOnError(t -> log.error("Error in Consumer 1: ", t)) // Xử lý lỗi nếu có
        .subscribe(); // Bắt đầu quá trình tiêu thụ các phần tử từ hàng đợi
  }

  @PostConstruct
  public void consumer2() throws InterruptedException {
    log.info("Starting message queue in Consumer2");
    this.blockingDequeReactive.takeElements() // Lấy các phần tử từ hàng đợi theo cơ chế LIFO
        .doOnNext(i -> log.info("Consumer 2 received message: {}", i)) // Xử lý mỗi phần tử nhận được
        .doOnError(t -> log.error("Error in Consumer 2: ", t)) // Xử lý lỗi nếu có
        .subscribe(); // Bắt đầu quá trình tiêu thụ các phần tử từ hàng đợi
  }

  @PostConstruct
  public void producer() throws InterruptedException {
    log.info("Starting message queue in Producer");
    Mono<Void> mono = Flux.range(1, 100) // Tạo Flux từ 1 đến 100
        .delayElements(Duration.ofSeconds(1)) // Giãn cách mỗi phần tử 1 giây
        .doOnNext(i -> log.info("Producing message: {}", i)) // In ra thông báo về việc sản xuất phần tử
        .flatMap(i -> this.blockingDequeReactive.add(Long.valueOf(i))) // Thêm phần tử vào hàng đợi
        .then(); // Chuyển đổi sang Mono<Void>
    mono.subscribe(); // Thực thi chuỗi các thao tác sản xuất phần tử (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }
}
