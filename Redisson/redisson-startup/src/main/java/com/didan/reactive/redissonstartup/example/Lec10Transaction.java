package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class Lec10Transaction {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;
  private RBucketReactive<Long> user1Balance;
  private RBucketReactive<Long> user2Balance;

  public Lec10Transaction(RedissonClient redissonClient, RedissonReactiveClient redissonReactiveClient) {
    this.redissonClient = redissonClient;
    this.redissonReactiveClient = redissonReactiveClient;
    this.user1Balance = redissonReactiveClient.getBucket("user:1:balance", LongCodec.INSTANCE); // Lấy bucket phản ứng cho số dư tài khoản người dùng 1
    this.user2Balance = redissonReactiveClient.getBucket("user:2:balance", LongCodec.INSTANCE); // Lấy bucket phản ứng cho số dư tài khoản người dùng 2

    user1Balance.set(100L) // Khởi tạo số dư tài khoản người dùng 1
        .then(user2Balance.set(0L)) // Khởi tạo số dư tài khoản người dùng 2
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }

  @PostConstruct
  public void transaction() throws InterruptedException {
    log.info("Starting transaction in Redisson");
    RTransactionReactive transaction = redissonReactiveClient.createTransaction(TransactionOptions.defaults()); // Tạo giao dịch phản ứng với các tùy chọn mặc định
    transfer(user1Balance, user2Balance, 50) // Chuyển 50 từ tài khoản người dùng 1 sang tài khoản người dùng 2
        .thenReturn(0) // Trả về 0 để tiếp tục chuỗi
        .map(i -> (5 / i)) // Giả lập lỗi chia cho 0 để kiểm tra rollback
        .then(transaction.commit()) // Cam kết giao dịch nếu không có lỗi
        .doOnError(err -> log.error("Transaction commit failed", err)) // Ghi log nếu cam kết giao dịch thất bại
        .onErrorResume(err -> transaction.rollback()) // Nếu có lỗi, thực hiện rollback giao dịch
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)

    Flux.zip(user1Balance.get(), user2Balance.get()) // Lấy số dư hiện tại của cả hai tài khoản
        .doOnNext(t -> log.info("User1 balance: {}, User2 balance: {}", t.getT1(), t.getT2())) // In ra số dư của cả hai tài khoản
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }


  Mono<Void> transfer(RBucketReactive<Long> from, RBucketReactive<Long> to, int amount) {
    return Flux.zip(from.get(), to.get()) // Lấy số dư hiện tại của cả hai tài khoản, zip: kết hợp hai Mono thành một Mono chứa một tuple
        .filter(t -> t.getT1() >= amount) // Kiểm tra nếu số dư tài khoản nguồn đủ để chuyển
        .flatMap(t -> from.set(t.getT1() - amount).thenReturn(t)) // Trừ số tiền từ tài khoản nguồn và trả về tuple ban đầu
        .flatMap(t -> to.set(t.getT2() + amount)) // Cộng số tiền vào tài khoản đích
        .then(); // Chuyển đổi sang Mono<Void>
  }


}
