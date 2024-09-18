package com.example.websocket.config;

import com.example.websocket.dto.Message;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, Message> producerFactory() { // Tạo ra một ProducerFactory để tạo ra một Producer
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Cấu hình Kafka server
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Cấu hình key serializer dùng để serialize key của message từ Object về bytes trước khi gửi tới Kafka
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Cấu hình value serializer dùng để serialize value của message từ Object về bytes trước khi gửi tới Kafka
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, Message> kafkaTemplate() { // Tạo ra một KafkaTemplate để gửi message tới Kafka
    return new KafkaTemplate<>(producerFactory()); // Tạo ra một KafkaTemplate với ProducerFactory đã được cấu hình ở trên
  }
}
