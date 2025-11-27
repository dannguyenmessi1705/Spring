package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec09BatchPipeline {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void batchPipeline() throws InterruptedException {
    log.info("Starting batch pipeline in Redisson");
    RBatchReactive batch = redissonReactiveClient.createBatch(BatchOptions.defaults()); // Tạo batch phản ứng với các tùy chọn mặc định
    RListReactive<Long> list = batch.getList("batch-list", LongCodec.INSTANCE); // Lấy danh sách phản ứng cho khóa "batch-list" trong batch
    RSetReactive<Long> set = batch.getSet("batch-set", LongCodec.INSTANCE); // Lấy tập hợp phản ứng cho khóa "batch-set" trong batch
    ///  batch có thể get nhiều cấu trúc dữ liệu khác nhau như RMapReactive, RQueueReactive, RDequeReactive, RSortedSetReactive, v.v.
    for (long i = 0; i < 100_000L; i++) {
      list.add(i); // Thêm phần tử vào danh sách trong batch
      set.add(i); // Thêm phần tử vào tập hợp trong batch
    }
    // Nếu không sử dụng batch, việc thêm 100K phần tử sẽ rất chậm do phải thực hiện nhiều lần gọi mạng. Sử dụng batch giúp gom nhiều lệnh lại và gửi một lần, cải thiện hiệu suất đáng kể.
    batch.execute()
        .doOnNext(r -> log.info("Batch executed successfully")) // Xử lý sau khi batch được thực thi thành công
        .subscribe(); // Thực thi batch và chuyển đổi sang Mono<Void> và subscribe để bắt đầu thực thi

  }


}
