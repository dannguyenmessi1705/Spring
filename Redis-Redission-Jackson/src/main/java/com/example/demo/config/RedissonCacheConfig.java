package com.example.demo.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
@EnableConfigurationProperties(RedissonConfigurationProperties.class) // Chỉ định class RedissonConfigurationProperties là class chứa các properties cấu hình
@ConditionalOnProperty(value = "app.cache.redis.enable", havingValue = "true") // Điều kiện để load class này, chỉ load khi app.cache.redis.enable = true
public class RedissonCacheConfig implements CachingConfigurer {

  @Value("${spring.application.name}") // Đọc giá trị của property spring.application.name
  private String appName;

  public SerializationPair<Object> getJdkSerialization() { // Hàm trả về SerializationPair<Object> với JdkSerializationRedisSerializer
    ClassLoader loader = Thread.currentThread()
        .getContextClassLoader(); // Lấy class loader của thread hiện tại
    JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
        loader); // Tạo JdkSerializationRedisSerializer với class loader vừa lấy
    return SerializationPair.fromSerializer(
        jdkSerializer); // Trả về SerializationPair<Object> với JdkSerializationRedisSerializer vừa tạo
  }

  public RedisCacheConfiguration createDefaultCacheConfiguration(
      long ttl) { // Hàm tạo RedisCacheConfiguration mặc định với thời gian sống là ttl
    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            getJdkSerialization()) // thuộc tính serializeValuesWith với giá trị trả về từ hàm getJdkSerialization()
        .computePrefixWith(cacheName -> {
          String prefix = appName + "::"; // Tạo prefix là appName + "::"
          if (StringUtils.hasText(cacheName)) {
            prefix += cacheName + "::"; // Nếu cacheName không rỗng thì thêm cacheName vào prefix
          }
          return prefix; // Trả về prefix vừa tạo
        })
        .entryTtl(Duration.ofMillis(ttl)); // Thiết lập thời gian sống của entry là ttl
  }

  @Bean
  @Primary
  public RedisTemplate<String, Object> redisTemplate(RedissonConnectionFactory cf) { // Hàm kết nối tới Redis để thực hiện các chức năng như đọc ghi, sử dụng cấu hình kết nối của Redisson
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>(); // Tạo RedisTemplate với key là String, value là Object
    redisTemplate.setKeySerializer(RedisSerializer.string()); // Đảm bảo key được serialize thành String trước khi lưu vào Redis
    redisTemplate.setHashKeySerializer(RedisSerializer.string()); // Đảm bảo hash key được serialize thành String trước khi lưu vào Redis
    redisTemplate.setConnectionFactory(cf); // Thiết lập connection factory cho RedisTemplate
    return redisTemplate;
  }

  @Bean(destroyMethod = "shutdown") // Bean với phương thức destroyMethod là "shutdown" dùng để đóng RedissonClient
  public RedissonClient redisson(RedissonConfigurationProperties props) {
    // Singleton instance of Redisson
    Config config = new Config();
    config.useSingleServer()
        .setAddress(props.getAddress())
        .setSubscriptionConnectionMinimumIdleSize(props.getSubscriptionConnectionMinimumIdleSize())
        .setSubscriptionConnectionPoolSize(props.getSubscriptionConnectionPoolSize())
        .setDatabase(props.getDatabase())
        .setConnectionPoolSize(props.getConnectionPoolSize())
        .setConnectionMinimumIdleSize(props.getConnectionMinimumIdleSize())
        .setDnsMonitoringInterval(props.getDnsMonitoringInterval())
        .setIdleConnectionTimeout(props.getIdleConnectionTimeout())
        .setConnectTimeout(props.getConnectTimeout())
        .setTimeout(props.getTimeout())
        .setRetryAttempts(props.getRetryAttempts())
        .setRetryInterval(props.getRetryInterval())
        .setUsername(props.getUsername())
        .setPassword(props.getPassword())
        .setSubscriptionsPerConnection(props.getSubscriptionsPerConnection());


    // Cluster
//    config.useClusterServers()
//        .setCheckSlotsCoverage(props.isCheckSlotsCoverage())
//        .setIdleConnectionTimeout(props.getIdleConnectionTimeout())
//        .setConnectTimeout(props.getConnectTimeout())
//        .setTimeout(props.getTimeout())
//        .setRetryAttempts(props.getRetryAttempts())
//        .setRetryInterval(props.getRetryInterval())
//        .setFailedSlaveReconnectionInterval(props.getFailedSlaveReconnectionInterval())
//        .setPassword(props.getPassword())
//        .setSubscriptionsPerConnection(props.getSubscriptionsPerConnection())
//        .setClientName(props.getClientName())
//        .setSubscriptionConnectionPoolSize(props.getSubscriptionConnectionPoolSize())
//        .setSlaveConnectionMinimumIdleSize(props.getSlaveConnectionMinimumIdleSize())
//        .setSlaveConnectionPoolSize(props.getSlaveConnectionPoolSize())
//        .setMasterConnectionMinimumIdleSize(props.getMasterConnectionMinimumIdleSize())
//        .setMasterConnectionPoolSize(props.getMasterConnectionPoolSize())
//        .setScanInterval(props.getScanInterval())
//        .setPingConnectionInterval(props.getPingConnectionInterval())
//        .setKeepAlive(props.isKeepAlive())
//        .setTcpNoDelay(props.isTcpNoDelay())
//        .setNodeAddresses(Arrays.asList(props.getNodeAddresses()));
    return Redisson.create(config);
  }

  @Bean
  public RedissonConnectionFactory redissonConnectionFactory(
      RedissonClient redissonClient) { // Hàm tạo RedissonConnectionFactory với tham số là RedissonClient, với redissonClient là cấu hình Redisson kết nối tới Redis
    return new RedissonConnectionFactory(redissonClient);
  }

  @Bean
  public CacheManager redisCacheManager(RedissonConnectionFactory redissonConnectionFactory,
      RedissonConfigurationProperties properties) {
    Map<String, RedisCacheConfiguration> config = new HashMap<>();
    RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redissonConnectionFactory);
    for (Map.Entry<String, Long> cachesWithTtl : properties.getCacheExpirations().entrySet()) {
      config.put(cachesWithTtl.getKey(), createDefaultCacheConfiguration(cachesWithTtl.getValue()));
    }
    return RedisCacheManager.builder(redisCacheWriter)
        .cacheDefaults(createDefaultCacheConfiguration(properties.getTtl()))
        .withInitialCacheConfigurations(config)
        .build();
  }

  /**
   * Sử dụng bằng cách đặt annotation @CacheConfig(cacheNames = {"cacheName"}, cacheManager= "redisCacheManager") trên class
   * Trong đó:
   * * cacheNames: Tên cache
   * * cacheManager: Tên bean CacheManager, có thể tạo nhiều CacheManager với tên khác nhau
   * Ngoài ra, có thể sử dụng @Cacheable(value="namefunc", key="#name", sync=true) trên method để cache kết quả trả về của method ví dụ như method này
   * Trong đó:
   * * value: Tên cache
   * * key: Biến sẽ nhận giá trị truyền vào từ method
   * * sync: Đồng bộ cache giúp tránh việc nhiều thread cùng truy cập cache cùng một lúc
   * Ví dụ:
   * @Cacheable(value="namefunc", key="#name", sync=true)
   * public String getName(String name) {
   *    return name; // name sẽ được key của Cache get đươợc trong name "namefunc" truyên xuống
   *    }
   *
   */

  @Override
  public CacheErrorHandler errorHandler() { // Tạo custom CacheErrorHandler để xử lý exception khi thao tác với cache
    return new RedisCachErrorHandle();
  }
}
