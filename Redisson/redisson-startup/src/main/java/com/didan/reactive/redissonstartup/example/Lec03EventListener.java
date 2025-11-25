package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec03EventListener {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void expireEventListener() throws InterruptedException {
    log.info("Starting Event Listener");
    RBucketReactive<String> bucket = redissonReactiveClient.getBucket("worker:1:name", StringCodec.INSTANCE); // Lấy bucket phản ứng cho khóa "worker:1:name"
    Mono<Void> set = bucket.set("alan", Duration.ofSeconds(10)); // Thiết lập giá trị "alan" cho bucket với thời gian sống 10 giây
    Mono<Void> get = bucket.get() // Lấy giá trị từ bucket
        .doOnNext(res -> log.info("res: {}", res)) // In ra giá trị nhận được
        .then(); // Chuyển đổi sang Mono<Void>
    Mono<Void> event = bucket.addListener(new ExpiredObjectListener() { // Thêm listener để lắng nghe sự kiện hết hạn của bucket
      @Override
      public void onExpired(String s) { // Xử lý khi đối tượng hết hạn
        log.info("expired object {}", s); // In ra thông báo key đã hết hạn
      }
    }).then();

    set.concatWith(get).concatWith(event) // Chuỗi các thao tác: thiết lập giá trị, lấy giá trị, thêm listener
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }

  @PostConstruct
  public void removeEventListener() throws InterruptedException {
    log.info("Starting Remove Event Listener");
    RBucketReactive<String> bucket = redissonReactiveClient.getBucket("worker:2:name", StringCodec.INSTANCE); // Lấy bucket phản ứng cho khóa "worker:2:name"
    Mono<Void> set = bucket.set("bob"); // Thiết lập giá trị "bob" cho bucket
    Mono<Void> get = bucket.get() // Lấy giá trị từ bucket
        .doOnNext(res -> log.info("res: {}", res)) // In ra giá trị nhận được
        .then(); // Chuyển đổi sang Mono<Void>
    Mono<Void> event = bucket.addListener(new DeletedObjectListener() { // Thêm listener để lắng nghe sự kiện hết hạn của bucket
      @Override
      public void onDeleted(String s) { // Xử lý khi đối tượng hết hạn
        log.info("deleted object {}", s); // In ra thông báo key đã hết hạn
      }
    }).then();

    set.concatWith(get).concatWith(event) // Chuỗi các thao tác: thiết lập giá trị, lấy giá trị, thêm listener
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }

  /**
   * Lưu ý: Để nhận được sự kiện hết hạn, bạn cần đảm bảo rằng Redis server của bạn được cấu hình đúng cách để hỗ trợ các sự kiện này.
   * Bạn có thể kiểm tra và thiết lập cấu hình này trong tệp cấu hình Redis (redis.conf) bằng cách đảm bảo rằng tùy chọn "notify-keyspace-events" được thiết lập đúng cách.
   * Ví dụ: Để nhận sự kiện hết hạn, bạn có thể thiết lập nó như sau:
   * notify-keyspace-events AKE
   */
}
