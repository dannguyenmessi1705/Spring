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
import org.springframework.kafka.support.mapping.AbstractJavaTypeMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {
  @Bean
  public ConsumerFactory<String, Message> consumerFactory() { // Tạo ra một ConsumerFactory để tạo ra một Consumer
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Cấu hình Kafka server
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "chat"); // Cấu hình group id
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Cấu hình key deserializer dùng để deserialize key của message (bytes) từ Kafka về Object

    // Nếu thông tin gửi sang Kafka chỉ là class đơn giảnm thì cỉ cần JsonDeserializer
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // Cấu hình value deserializer dùng để deserialize value của message (bytes) từ Kafka về Object

    /** Nếu thông tin gửi sang Kafka là một class phức tạp dạng kiểu List<Class>,... thì cần phải cấu hình như sau
    * configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // Cấu hình value deserializer dùng để deserialize value của message (bytes) từ Kafka về Object, dạng ErrorHandlingDeserializer để xử lý exception khi deserialize
    * configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomJsonDeserializer.class); // Cấu hình value của ExceptionHandlingDeserializer về CustomJsonDeserializer để xử lý exception khi deserialize
    * configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.didan.spring_account_transaction.dto.request.TransactionRequestDTO"); // Cấu hình value default type của JsonDeserializer, chính là class cần deserialize (truyền package name của class cần deserialize)

    */
  
    return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(Message.class)); // Tạo ra một ConsumerFactory với cấu hình và deserializer đã được cấu hình
  }

  /**
   * CustomJsonDeserializer để deserialize dữ liệu từ Kafka về Object cho class
   * phức tạp
   * public static class CustomJsonDeserializer extends JsonDeserializer<Message>
   * {
   * public CustomJsonDeserializer() {
   * this.typeMapper.addTrustedPackages("*");
   * ((AbstractJavaTypeMapper) this.getTypeMapper()).setUseForKey(true);
   * }
   * }
  */

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() { // Tạo ra một KafkaListenerContainerFactory để tạo ra một KafkaListenerContainer
    ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory()); // Cấu hình ConsumerFactory cho KafkaListenerContainerFactory
    return factory;
  }
}
