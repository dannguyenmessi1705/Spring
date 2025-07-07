package com.didan.elastic.processor;

import com.didan.elastic.dto.ProductCreatedEvent;
import com.didan.elastic.dto.ProductDeletedEvent;
import com.didan.elastic.dto.ProductUpdatedEvent;
import com.didan.elastic.service.ElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author dannd1
 * @since 7/7/2025
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

  private final ElasticsearchSyncService elasticsearchSyncService;

  @EventListener
  @Async
  public void handleProductCreated(ProductCreatedEvent event) {
    try {
      log.info("Publishing product created event for product ID: {}", event.getProductId());
      elasticsearchSyncService.syncProduct(event.getProductId());
    } catch (Exception e) {
      log.info("Error processing ProductCreatedEvent: {}", e.getMessage(), e);
    }
  }

  @EventListener
  @Async
  public void handleProductUpdated(ProductUpdatedEvent event) {
    try {
      log.info("Handling product updated event for ID: {}", event.getProductId());
      elasticsearchSyncService.syncProduct(event.getProductId());
    } catch (Exception e) {
      log.error("Error handling product updated event", e);
    }
  }

  @EventListener
  @Async
  public void handleProductDeleted(ProductDeletedEvent event) {
    try {
      log.info("Handling product deleted event for ID: {}", event.getProductId());
      elasticsearchSyncService.deleteProductDocument(event.getProductId());
    } catch (Exception e) {
      log.error("Error handling product deleted event", e);
    }
  }
}
