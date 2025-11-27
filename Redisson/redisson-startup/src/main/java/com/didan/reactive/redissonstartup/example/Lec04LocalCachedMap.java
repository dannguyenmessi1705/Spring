package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.api.options.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.options.LocalCachedMapOptions.SyncStrategy;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec04LocalCachedMap {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;
  private static final LocalCachedMapOptions<Integer, Student> mapOptions; // Thiết lập tùy chọn cho bản đồ được lưu trong bộ nhớ đệm cục bộ

  static {
    mapOptions = LocalCachedMapOptions
        .<Integer, Student>name("students") // Đặt tên cho bản đồ được lưu trong bộ nhớ đệm cục bộ
        .codec(new TypedJsonJacksonCodec(Integer.class, Student.class)) // Sử dụng codec JSON cho key kiểu Integer và value kiểu Student
        .syncStrategy(SyncStrategy.UPDATE) // Cập nhật các thay đổi nếu các node khác thay đổi dữ liệu
        .reconnectionStrategy(ReconnectionStrategy.CLEAR); // Xóa bộ nhớ đệm cục bộ khi kết nối lại
  }


  @PostConstruct
  public void appServer1() throws InterruptedException {
    log.info("Starting localCachedMap in Server1");
    RLocalCachedMap<Integer, Student> studentsMap = redissonClient.getLocalCachedMap(mapOptions); // Lấy bản đồ được lưu trong bộ nhớ đệm cục bộ với các tùy chọn đã thiết lập
    Student s1 = new Student("John Doe", 20, "123 Main St", List.of(1, 2, 3, 4, 5)); // Tạo đối tượng Student
    Student s2 = new Student("Jane Smith", 22, "456 Elm St", List.of(6, 7, 8, 9, 10)); // Tạo đối tượng Student

    studentsMap.put(1, s1); // Thêm Student s1 vào bản đồ với key 1
    studentsMap.put(2, s2); // Thêm Student s2 vào bản đồ với key 2

    Flux.interval(Duration.ofSeconds(10))
        .doOnNext(i -> log.info("{} ==> {}", i, studentsMap.get(1))) // In ra giá trị của key 1 mỗi 2 giây
        .subscribe(); // Thực thi chuỗi các thao tác (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }

  @PostConstruct
  public void appServer2() throws InterruptedException {
    log.info("Starting localCachedMap in Server2");
    RLocalCachedMap<Integer, Student> studentsMap = redissonClient.getLocalCachedMap(mapOptions); // Lấy bản đồ được lưu trong bộ nhớ đệm cục bộ với các tùy chọn đã thiết lập
    Student s1 = new Student("John Doe Updated", 20, "123 Main St", List.of(1, 2, 3, 4, 5)); // Tạo đối tượng Student

    studentsMap.put(1, s1); // Thêm Student s1 vào bản đồ với key 1

    // Sau khi cập nhật, server1 sẽ nhận được cập nhật này do chiến lược đồng bộ hóa được đặt là UPDATE
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
