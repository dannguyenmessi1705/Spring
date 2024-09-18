package com.example.websocket.service;

import com.example.websocket.dto.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class Sender {
  private final KafkaTemplate<String, Message> kafkaTemplate; // Đối tượng này giúp gửi message tới Kafka

  public void send(String topic, Message message) {
    log.info("Message sent to Kafka: {}", message);
    kafkaTemplate.send(topic, message); // Gửi message tới Kafka với topic và message cần gửi
  }

}
