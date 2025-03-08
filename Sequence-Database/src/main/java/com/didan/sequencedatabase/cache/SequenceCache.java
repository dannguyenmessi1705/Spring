package com.didan.sequencedatabase.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SequenceCache {
  @PersistenceContext
  private EntityManager entityManager;

  private static final long expireTime = 2;
  private static final int batchSize = 100;

  private Cache<String, Queue<Long>> cache;
  private static final String CACHE_KEY = "transaction_sequence";

  @PostConstruct
  public void init() {
    this.cache = Caffeine.newBuilder()
        .maximumSize(batchSize)
        .expireAfterWrite(expireTime, TimeUnit.DAYS)
        .evictionListener((key, value, cause) -> log.info("Evicting cache key: {}", key))
        .build(key -> fetchNextBatchFromDB());
  }

  @Transactional
  public synchronized String generateTransactionId(int size) {
    Queue<Long> idQueue = cache.getIfPresent(CACHE_KEY);

    if (idQueue == null || idQueue.isEmpty()) {
      idQueue = fetchNextBatchFromDB();
      cache.put(CACHE_KEY, idQueue);
    }

    Long nextValue = idQueue.poll();
    if (nextValue == null) {
      idQueue = fetchNextBatchFromDB();
      cache.put(CACHE_KEY, idQueue);
      nextValue = idQueue.poll();
    }

    if (nextValue == null) {
      throw new RuntimeException("Cannot generate transaction id");
    }
    log.info("Generated transaction id: {}", nextValue);
    return String.format("%08d", nextValue);
  }

  private Queue<Long> fetchNextBatchFromDB() {
    log.info("Fetching next batch of transaction id from DB");
    Queue<Long> idQueue = new ConcurrentLinkedQueue<>();

    for (int i = 0; i < batchSize; i++) {
      long nextId = ((Number) entityManager.createNativeQuery("SELECT NEXT VALUE FOR transaction_sequence").getSingleResult()).longValue();
      idQueue.add(nextId);
    }
    return idQueue;
  }
}
