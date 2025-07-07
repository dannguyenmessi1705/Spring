package com.didan.elastic.service;

import com.didan.elastic.entity.Product;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dannd1
 * @since 7/7/2025
 */
@Slf4j
@Component
public class ProductEntityListener {

  private static ProductEventPublisher eventPublisher;

  @Autowired
  public ProductEntityListener(ProductEventPublisher eventPublisher) {
    ProductEntityListener.eventPublisher = eventPublisher;
  }

  @PostPersist
  public void afterCreate(Product product) {
    if (eventPublisher != null) {
      eventPublisher.publishProductCreated(product.getId());
    }
  }

  @PostUpdate
  public void afterUpdate(Product product) {
    if (eventPublisher != null) {
      eventPublisher.publishProductUpdated(product.getId());
    }
  }

  @PostRemove
  public void afterDelete(Product product) {
    if (eventPublisher != null) {
      eventPublisher.publishProductDeleted(product.getId());
    }
  }

}
