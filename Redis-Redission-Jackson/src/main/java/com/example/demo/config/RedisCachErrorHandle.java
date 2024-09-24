package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class RedisCachErrorHandle implements CacheErrorHandler {

  @Override
  public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
    hanldeTimeOutException(exception);
    log.info("Unable to get into cache {}:{}", cache.getName(), exception.getMessage());
  }


  @Override
  public void handleCachePutError(RuntimeException exception, Cache cache, Object key,
      Object value) {
    hanldeTimeOutException(exception);
    log.info("Unable to put into cache {}:{}", cache.getName(), exception.getMessage());
  }

  @Override
  public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
    hanldeTimeOutException(exception);
    log.info("Unable to evict cache {}:{}:{}", cache.getName(), key, exception.getMessage());
  }

  @Override
  public void handleCacheClearError(RuntimeException exception, Cache cache) {
    hanldeTimeOutException(exception);
    log.info("Unable to clean cache {}:{}", cache.getName(), exception.getMessage());
  }

  private void hanldeTimeOutException(RuntimeException exception) {
    log.error(exception.getMessage(), exception);
  }
}
