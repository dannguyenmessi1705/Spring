package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec01KeyValue {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void keyValueAccess() throws InterruptedException {
    log.info("Starting Key Value Access");
    RBucketReactive<String> bucket = redissonReactiveClient.getBucket("user:1:name", StringCodec.INSTANCE); // Lấy bucket phản ứng cho khóa "user:1:name"
    Mono<Void> set = bucket.set("sam"); // Thiết lập giá trị "sam" cho bucket
    // Để set thêm thời gian sống cho key (TTL - Time To Live)
    // Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10)); // Thiết lập giá trị "sam" với thời gian sống 10 giây
    Mono<Void> get = bucket.get() // Lấy giá trị từ bucket
        .doOnNext(res -> log.info("res: {}", res)) // In ra giá trị nhận được
        .then(); // Chuyển đổi sang Mono<Void>
    Mono<Boolean> expire = bucket.expire(Duration.ofSeconds(30)); // Thiết lập thời gian sống cho bucket là 30 giây
    Mono<Long> ttl = bucket.remainTimeToLive()
        .doOnNext(t -> log.info("TTL: {}", t)); // In ra thời gian sống còn lại

    Student student = new Student("John Doe", 20, "123 Main St", Arrays.asList(1, 2, 3, 4, 5)); // Tạo đối tượng Student
    RBucketReactive<Student> studentBucket = redissonReactiveClient.getBucket("student:1", new TypedJsonJacksonCodec(Student.class)); // Lấy bucket phản ứng cho đối tượng Student với codec JSON
    Mono<Void> setStudent = studentBucket.set(student); // Thiết lập đối tượng Student vào bucket
    Mono<Student> getStudent = studentBucket.get()
        .doOnNext(s -> log.info("Student: {}", s));

    set
        .then(get) // Chuỗi các thao tác: thiết lập giá trị, lấy giá trị
        .then(expire) // Thiết lập thời gian sống
        .then(ttl) // Lấy thời gian sống còn lại
        .subscribe(); // Thực thi chuỗi các thao tác thiết lập và lấy giá trị (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)

    setStudent
        .then(getStudent) // Chuỗi các thao tác: thiết lập đối tượng Student, lấy đối tượng Student
        .subscribe(); // Thực thi chuỗi các thao tác thiết lập và lấy đối tượng Student
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class Student {

    private String name;
    private int age;
    private String address;
    private List<Integer> marks;
  }

}
