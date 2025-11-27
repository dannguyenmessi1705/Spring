package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec05ListQueueStack {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void list() throws InterruptedException {
    log.info("Starting list in Redisson");
    RListReactive<Long> list = redissonReactiveClient.getList("number", LongCodec.INSTANCE); // Lấy danh sách phản ứng cho khóa "number" với codec Long
    List<Long> longList = LongStream
        .rangeClosed(1, 10) // Tạo luồng dài từ 1 đến 10
        .boxed() // Chuyển đổi từ LongStream sang Stream<Long>
        .collect(Collectors.toList()); // Thu thập các phần tử vào một danh sách
    Mono<Void> set = list.addAll(longList).then(); // Thêm tất cả các phần tử từ longList vào danh sách và chuyển đổi sang Mono<Void>
    Mono<Void> size = list.size()
        .doOnNext(i -> log.info("List Size: {}", i)) // In ra kích thước của danh sách
        .then();
    set.concatWith(size).subscribe();
  }

  @PostConstruct
  public void queue() throws InterruptedException {
    log.info("Starting queue in Redisson");
    RQueueReactive<Long> queue = redissonReactiveClient.getQueue("number", LongCodec.INSTANCE); // Lấy hàng đợi phản ứng cho khóa "number" với codec Long
    Mono<Void> poll = queue
        .poll() // Lấy và loại bỏ phần tử đầu tiên từ hàng đợi
        .repeat(3) // Lặp lại thao tác poll 3 lần
        .doOnNext(i -> log.info("Polled from Queue: {}", i)) // In ra phần tử được lấy từ hàng đợi
        .then();
    poll.subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }

  @PostConstruct
  public void stack() throws InterruptedException {
    log.info("Starting stack in Redisson");
    RDequeReactive<Long> stack = redissonReactiveClient.getDeque("number", LongCodec.INSTANCE); // Lấy hàng đợi phản ứng kép (stack) cho khóa "number" với codec Long
    Mono<Void> pop = stack
        .pollLast() // Lấy và loại bỏ phần tử cuối cùng từ hàng đợi
        .repeat(3) // Lặp lại thao tác remove 3 lần
        .doOnNext(i -> log.info("Popped from Stack: {}", i)) // In ra phần tử được lấy từ hàng đợi
        .then();
    pop.subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }
}
