package com.didan.reactive.redissonstartup.realtime.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService implements WebSocketHandler {

  // Redisson Reactive Client để tương tác với Redis trong môi trường phản ứng
  private final RedissonReactiveClient redissonReactiveClient;

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    String room = getChatRoomName(session); // Lấy tên phòng chat từ phiên WebSocket

    RTopicReactive topic = redissonReactiveClient.getTopic(room, StringCodec.INSTANCE); // Lấy chủ đề phản ứng từ RedissonReactiveClient dựa trên tên phòng chat
    RListReactive<String> list = redissonReactiveClient.getList("history:" + room, StringCodec.INSTANCE); // Lấy danh sách phản ứng từ RedissonReactiveClient để lưu trữ lịch sử tin nhắn

    // Subscribe để nhận tin nhắn từ phiên WebSocket và xuất bản chúng lên topic
    session.receive() // Nhận tin nhắn từ phiên WebSocket
        .map(WebSocketMessage::getPayloadAsText) // Chuyển đổi tin nhắn WebSocket thành chuỗi văn bản
        .flatMap(msg -> list.add(msg).then(topic.publish(msg))) // Thêm tin nhắn vào history list và xuất bản nó lên topic
        .doOnError(err -> log.error("Error sending message", err)) // Ghi log lỗi nếu có
        .doFinally(s -> log.error("Subscriber finally {}", s)) // Ghi log khi quá trình hoàn tất
        .subscribe(); // Bắt đầu quá trình nhận tin nhắn từ phiên WebSocket và xử lý chúng

    // Lắng nghe tin nhắn từ topic và gửi chúng đến phiên WebSocket
    Flux<WebSocketMessage> flux = topic.getMessages(String.class)
        .startWith(list.iterator()) // Bắt đầu với các tin nhắn lịch sử từ danh sách
        .map(session::textMessage) // Chuyển đổi chuỗi văn bản thành tin nhắn WebSocket
        .doOnError(err -> log.error("Error sending message", err)) // Ghi log lỗi nếu có
        .doFinally(s -> log.error("Publisher finally {}", s)); // Ghi log khi quá trình hoàn tất

    return session.send(flux); // Gửi các tin nhắn từ flux đến phiên WebSocket
  }

  private String getChatRoomName(WebSocketSession session) {
    URI uri = session.getHandshakeInfo().getUri(); // Lấy URI từ thông tin bắt tay của phiên WebSocket
    return UriComponentsBuilder.fromUri(uri) // Xây dựng thành phần URI từ URI ban đầu
        .build()
        .getQueryParams() // Lấy tham số truy vấn từ URI
        .toSingleValueMap() // Chuyển đổi tham số truy vấn thành bản đồ giá trị đơn
        .getOrDefault("room", "default"); // Lấy giá trị của tham số "room" hoặc trả về "default" nếu không tồn tại
  }
}
