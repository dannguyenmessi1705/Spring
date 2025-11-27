package com.didan.reactive.redissonstartup.example;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class Lec08PubSub {

  private final RedissonClient redissonClient;
  private final RedissonReactiveClient redissonReactiveClient;

  @PostConstruct
  public void sub1() throws InterruptedException {
    log.info("Starting pub/sub in sub1");
    RTopicReactive topic = redissonReactiveClient.getTopic("room", StringCodec.INSTANCE); // Lấy chủ đề phản ứng cho khóa "room" với codec String
    topic.getMessages(String.class) // Lấy các tin nhắn từ chủ đề (tin nhắn có kiểu String)
        .doOnNext(msg -> log.info("Sub1 received message: {}", msg)) // Xử lý mỗi tin nhắn nhận được
        .doOnError(err -> log.error("Error received message", err)) // Xử lý lỗi nếu có
        .subscribe(); // Bắt đầu quá trình lắng nghe tin nhắn từ chủ đề
  }

  @PostConstruct
  public void sub2() throws InterruptedException {
    log.info("Starting pub/sub in sub2");
    RTopicReactive topic = redissonReactiveClient.getTopic("room", StringCodec.INSTANCE); // Lấy chủ đề phản ứng cho khóa "room" với codec String
    topic.getMessages(String.class) // Lấy các tin nhắn từ chủ đề (tin nhắn có kiểu String)
        .doOnNext(msg -> log.info("Sub2 received message: {}", msg)) // Xử lý mỗi tin nhắn nhận được
        .doOnError(err -> log.error("Error received message", err)) // Xử lý lỗi nếu có
        .subscribe(); // Bắt đầu quá trình lắng nghe tin nhắn từ chủ đề
  }

  // Chúng ta có thể sử dụng RPatternTopicReactive để lắng nghe nhiều chủ đề cùng một lúc nếu cần thiết bằng cách sử dụng PATTERN để bắt các chủ đề phù hợp.
  @PostConstruct
  public void patternSub() throws InterruptedException {
    log.info("Starting pub/sub in pattern sub");
    RPatternTopicReactive patetrnTopic = redissonReactiveClient.getPatternTopic("r*", StringCodec.INSTANCE); // Lấy chủ đề mẫu phản ứng cho các khóa bắt đầu bằng "r" với codec String
    patetrnTopic.addListener(
        String.class,
        (pattern, topic, msg) -> {
          log.info("Pattern Sub received message: {} from topic: {} with pattern: {}", msg, topic, pattern); // Xử lý mỗi tin nhắn nhận được cùng với chủ đề và mẫu
        }
    ).subscribe(); // Bắt đầu quá trình lắng nghe tin nhắn từ các chủ đề phù hợp với mẫu
  }

  // Để publish tin nhắn, bạn có thể sử dụng đoạn mã sau trong một phương thức @PostConstruct khác hoặc từ một nơi khác trong ứng dụng của bạn.
  // Hoặc trong redis-cli bạn có thể sử dụng lệnh: `PUBLISH room "Hello from redis-cli"`
  @PostConstruct
  public void publishMessages() throws InterruptedException {
    log.info("Starting pub/sub in publisher");
    RTopicReactive topic = redissonReactiveClient.getTopic("room", StringCodec.INSTANCE); // Lấy chủ đề phản ứng cho khóa "room" với codec String
    Mono<Void> mono = Flux.range(1, 10) // Tạo Flux từ 1 đến 10
        .delayElements(java.time.Duration.ofSeconds(1)) // Giãn cách mỗi phần tử 1 giây
        .flatMap(i -> {
          String message = "Message " + i;
          log.info("Publishing message: {}", message); // In ra thông báo về việc gửi tin nhắn
          return topic.publish(message); // Gửi tin nhắn đến chủ đề
        })
        .then(); // Chuyển đổi sang Mono<Void>
    mono.subscribe(); // Thực thi chuỗi các thao tác gửi tin nhắn (Trong Reactive (phản ứng) cần có sự kiện subscribe để bắt đầu thực thi)
  }
}
