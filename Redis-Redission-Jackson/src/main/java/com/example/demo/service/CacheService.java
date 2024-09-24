package com.example.demo.service;

import com.example.demo.utils.ObjectMapperUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {
  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${spring.application.name}")
  private String appName;

  public <T> void setCache(String cacheName, String key, T value) {
    setCache(cacheName, key, value, -1);
  }

  public <T> void setCache(String cacheName, String key, T value, long timeout) {
    String keyGen = this.keyGen(cacheName, key);
    log.info("RedisCacheTemplate put: cacheName = {}, key = {}, value = {}", cacheName, keyGen,
        ObjectMapperUtils.toJson(value));
    try {
      String valueStr = ObjectMapperUtils.toJson(value);
      redisTemplate.opsForValue().set(keyGen, valueStr);
      if (timeout != -1) {
        redisTemplate.expire(keyGen, timeout, TimeUnit.SECONDS);
      }
    } catch (Exception ex) {
      log.error("SetCache: Redis cache exception", ex);
    }
  }

  public <T> T getCache(String cacheName, String key, Class<T> objectClass) {
    String keyGen = this.keyGen(cacheName, key);
    log.info("RedisCacheTemplate get: cacheName = {}, key = {}", cacheName, keyGen);
    String valueStr = (String) redisTemplate.opsForValue().get(keyGen);
    if (valueStr == null) {
      log.info("Key {} does not exist", keyGen);
      return null;
    } else {
      try {
        return ObjectMapperUtils.fromJson(valueStr, objectClass);
      } catch (Exception ex) {
        log.error("GetCache: Redis cache exception", ex);
        return null;
      }
    }
  }

  public void deleteCache(String cacheName, String key) {
    String keyGen = this.keyGen(cacheName, key);
    log.info("RedisCacheTemplate delete: cacheName = {}, key = {}", cacheName, keyGen);
    Boolean isDeleted = redisTemplate.delete(keyGen);
    if (!Boolean.TRUE.equals(isDeleted)) {
      log.info("Key {} does not exist or delete fail", keyGen);
    }
  }

  private String keyGen(String cacheName, Object key) {
    return StringUtils.hasLength(cacheName) ? this.appName + ":" + cacheName + ":" + key : key.toString();
  }

}
