package com.didan.reactive.redissonstartup.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  @Bean(destroyMethod = "shutdown") // Đảm bảo RedissonClient được tắt đúng cách khi ứng dụng dừng, gọi phương thức shutdown của RedissonClient
  public RedissonClient getClient() {
    Config config = new Config(); // Tạo mới cấu hình Redisson
    config.useSingleServer() // Sử dụng chế độ máy chủ đơn
        .setAddress("redis://127.0.0.1:6379"); // Đặt địa chỉ máy chủ Redis (Lưu ý: Thêm tiền tố "redis://")
    return Redisson.create(config); // Tạo và trả về RedissonClient
  }

  // Bean để cung cấp RedissonReactiveClient
  @Bean(destroyMethod = "shutdown") // Đảm bảo RedissonClient được tắt đúng cách khi ứng dụng dừng, gọi phương thức shutdown của RedissonClient
  public RedissonReactiveClient getReactiveClient() {
    return getClient().reactive(); // Tạo RedissonReactiveClient từ RedissonClient
  }
}