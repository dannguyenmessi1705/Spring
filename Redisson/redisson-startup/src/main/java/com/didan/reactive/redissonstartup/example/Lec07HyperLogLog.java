package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec07HyperLogLog {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void hyperLogLog() throws InterruptedException {
    log.info("Starting HyperLogLog in Redisson");
    RHyperLogLogReactive<Long> hyperLogLog = redissonReactiveClient.getHyperLogLog("hll", LongCodec.INSTANCE); // Lấy HyperLogLog phản ứng cho khóa "hll"
    List<Long> l1 = LongStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList()); // Tạo luồng dài từ 1 đến 10000
    List<Long> l2 = LongStream.rangeClosed(5000, 15000).boxed().collect(Collectors.toList()); // Tạo luồng dài từ 5000 đến 15000
    List<Long> l3 = LongStream.rangeClosed(10000, 20000).boxed().collect(Collectors.toList()); // Tạo luồng dài từ

    Mono<Void> add = Flux.just(l1, l2, l3)
        .flatMap(hyperLogLog::addAll)
        .then();

    Mono<Void> count = hyperLogLog.count()
        .doOnNext(i -> log.info("HyperLogLog Count: {}", i)) // In ra kích thước ước tính của HyperLogLog
        .then();

    add.then(count).subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi
  }
}
