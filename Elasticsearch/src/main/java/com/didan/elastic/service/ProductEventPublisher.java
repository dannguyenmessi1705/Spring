package com.didan.elastic.service;

import com.didan.elastic.dto.ProductCreatedEvent;
import com.didan.elastic.dto.ProductDeletedEvent;
import com.didan.elastic.dto.ProductUpdatedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author dannd1
 * @since 7/7/2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  /**
   * Phát sự kiện khi sản phẩm được tạo mới.
   * @param productId
   */
  public void publishProductCreated(Long productId) {
    log.info("Publishing product created event for product ID: {}", productId);
    eventPublisher.publishEvent(new ProductCreatedEvent(productId, LocalDateTime.now()));
  }

  /**
   * Phát sự kiện khi sản phẩm được cập nhật.
   * @param productId
   */
  public void publishProductUpdated(Long productId) {
    log.info("Publishing product updated event for product ID: {}", productId);
    eventPublisher.publishEvent(new ProductUpdatedEvent(productId, LocalDateTime.now()));
  }

  /**
   * Phát sự kiện khi sản phẩm bị xóa.
   * @param productId
   */
  public void publishProductDeleted(Long productId) {
    log.info("Publishing product deleted event for product ID: {}", productId);
    eventPublisher.publishEvent(new ProductDeletedEvent(productId, LocalDateTime.now()));
  }
}
