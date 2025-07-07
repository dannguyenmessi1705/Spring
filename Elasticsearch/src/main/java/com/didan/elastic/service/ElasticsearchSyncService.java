package com.didan.elastic.service;

import com.didan.elastic.dto.SyncStatus;
import com.didan.elastic.entity.Product;
import com.didan.elastic.entity.ProductDocument;
import com.didan.elastic.repository.ProductDocumentRepository;
import com.didan.elastic.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dannd1
 * @since 7/3/2025
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;

  @Value("${app.elasticsearch.sync.batch-size}")
  private int batchSize;

  /**
   * Đồng bộ dữ liệu lần đầu hoặc cần refresh lại dữ liệu từ cơ sở dữ liệu sang Elasticsearch.
   */
  public void fullSync() {
    log.info("Starting full synchronization of products from PostgreSQL to Elasticsearch");

    try {
      productDocumentRepository.deleteAll(); // Xóa tất cả dữ liệu cũ trong Elasticsearch
      List<Product> allProducts = productRepository.findAll();
      log.info("Find {} products in PostgreSQL", allProducts.size());

      List<ProductDocument> productDocuments = new ArrayList<>();

      for (int i = 0; i < allProducts.size(); i++) {
        productDocuments.add(new ProductDocument(allProducts.get(i)));
        if (productDocuments.size() >= batchSize || i == allProducts.size() - 1) { // Nếu số lượng vượt quá size
          productDocumentRepository.saveAll(productDocuments);
          log.info("Synced batch of {} products", productDocuments.size());
          productDocuments.clear();
        }
      }

      log.info("Full synchronization completed successfully");
    } catch (Exception e) {
      log.error("Full synchronization failed", e);
      throw new RuntimeException("Full synchronization failed", e);
    }
  }

  /**
   * Đồng bộ những dữ liệu đã thay đổi kể từ lần đồng bộ cuối cùng.
   */
  public void incrementalSync(LocalDateTime lastSyncTime) {
    log.info("Starting incremental synchronization of products from PostgreSQL to Elasticsearch since {}", lastSyncTime);

    try {
      List<Product> updatedProducts = productRepository.findUpdatedSince(lastSyncTime);

      if (updatedProducts.isEmpty()) {
        log.info("No new updates found, skipping synchronization");
        return;
      }

      log.info("Found {} updated products since last sync", updatedProducts.size());
      List<ProductDocument> productDocuments = updatedProducts.stream()
          .map(ProductDocument::new)
          .collect(Collectors.toList());

      productDocumentRepository.saveAll(productDocuments);

      for (Product product : updatedProducts) {
        productDocuments.add(new ProductDocument(product));
      }
      log.info("Incremental synchronization completed successfully with {} products", productDocuments.size());
    } catch (Exception e) {
      log.error("Incremental synchronization failed", e);
      throw new RuntimeException("Incremental synchronization failed", e);
    }
  }

  /**
   * Đồng bộ 1 dữ liệu sản phẩm cụ thể từ PostgreSQL sang Elasticsearch.
   */
  public void syncProduct(Long productId) {
    try {
      Optional<Product> product = productRepository.findById(productId);

      if (product.isPresent()) {
        ProductDocument productDocument = new ProductDocument(product.get());
        productDocumentRepository.save(productDocument);
        log.info("Synced product with id={}", productDocument.getId());
      } else {
        productDocumentRepository.deleteById(productId.toString());
        log.info("Deleted product with id={} as it no longer exists in PostgreSQL", productId);
      }
    } catch (Exception e) {
      log.error("Error during synchronization of product with ID: {}", productId, e);
      throw new RuntimeException("Product synchronization failed", e);
    }
  }

  /**
   * Xoá dữ liệu sản phẩm khỏi Elasticsearch khi sản phẩm bị xoá khỏi PostgreSQL.
   */
  public void deleteProductDocument(Long productId) {
    try {
      productDocumentRepository.deleteById(productId.toString());
      log.info("Deleted product document with id={}", productId);
    } catch (Exception e) {
      log.error("Error deleting product document with ID: {}", productId, e);
    }
  }

  /**
   * Kiểm tra tình trạng đồng bộ hoá giữa PostgreSQL và Elasticsearch.
   */
  public SyncStatus getSyncStatus() {
    try {
      long postgresCount = productRepository.count();
      long elasticCount = productDocumentRepository.count();
      return new SyncStatus(postgresCount, elasticCount, postgresCount == elasticCount);
    } catch (Exception e) {
      log.info("Error getting sync status", e);
      return new SyncStatus(0, 0, false);
    }
  }

  /**
   * Scheduled đồng bộ hoá dữ liệu định kỳ.
   */
  @Scheduled(fixedDelay = 300000) // 5 phút
  public void scheduledSync() {
    if (isScheduledSyncEnabled()) {
      LocalDateTime lastSyncTime = getLastSyncTime();
      if (lastSyncTime != null) {
        incrementalSync(lastSyncTime);
        updateLastSyncTime(LocalDateTime.now());
      }
    }
    log.info("Starting scheduled synchronization of products from PostgreSQL to Elasticsearch");
  }

  private boolean isScheduledSyncEnabled() {
    return true;
  }

  private LocalDateTime getLastSyncTime() {
    // Có thể lưu trong database hoặc Redis
    // Tạm thời return 1 giờ trước
    return LocalDateTime.now().minusHours(1);
  }

  private void updateLastSyncTime(LocalDateTime lastSyncTime) {
    // Cập nhật thời gian đồng bộ cuối cùng vào cơ sở dữ liệu hoặc cache
    log.info("Updating last sync time to {}", lastSyncTime);
  }
}
