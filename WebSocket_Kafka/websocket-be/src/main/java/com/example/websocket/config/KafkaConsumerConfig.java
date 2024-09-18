package com.example.websocket.config;

import com.example.websocket.dto.Message;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {
  @Bean
  public ConsumerFactory<String, Message> consumerFactory() { // Tạo ra một ConsumerFactory để tạo ra một Consumer
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Cấu hình Kafka server
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "chat"); // Cấu hình group id
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Cấu hình key deserializer dùng để deserialize key của message (bytes) từ Kafka về Object
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // Cấu hình value deserializer dùng để deserialize value của message (bytes) từ Kafka về Object
    return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(Message.class)); // Tạo ra một ConsumerFactory với cấu hình và deserializer đã được cấu hình
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() { // Tạo ra một KafkaListenerContainerFactory để tạo ra một KafkaListenerContainer
    ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory()); // Cấu hình ConsumerFactory cho KafkaListenerContainerFactory
    return factory;
  }
}
