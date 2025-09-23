package com.didan.elastic.controller;

import com.didan.elastic.dto.SyncStatus;
import com.didan.elastic.entity.Product;
import com.didan.elastic.service.ElasticsearchSyncService;
import com.didan.elastic.service.ProductService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dannd1
 * @since 7/7/2025
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final ElasticsearchSyncService elasticsearchSyncService;

  /**
   * Tạo mới một sản phẩm.
   * @param product
   * @return
   */
  @PostMapping
  public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
    Product createdProduct = productService.createProduct(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
  }

  /**
   * Tạo mới nhiều sản phẩm cùng lúc.
   * @param products
   * @return
   */
  @PostMapping("/bulk")
  public ResponseEntity<List<Product>> createProducts(@Valid @RequestBody List<Product> products) {
    List<Product> createdProducts = productService.createProducts(products);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProducts);
  }

  /**
   * Lấy thông tin sản phẩm theo ID.
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return productService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Lấy danh sách tất cả sản phẩm với phân trang và sắp xếp.
   * @param page
   * @param size
   * @param sortBy
   * @param sortDir
   * @return
   */
  @GetMapping
  public ResponseEntity<Page<Product>> getAllProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("desc") ?
        Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Product> products = productService.findAllProducts(pageable);

    return ResponseEntity.ok(products);
  }

  /**
   * Cập nhật thông tin sản phẩm theo ID.
   * @param id
   * @param product
   * @return
   */
  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable Long id,
      @Valid @RequestBody Product product) {
    Product updatedProduct = productService.updateProduct(id, product);
    return ResponseEntity.ok(updatedProduct);
  }

  /**
   * Xoá sản phẩm theo ID.
   * @param id
   * @return
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }

  // Search Operations (sử dụng Elasticsearch)

  /**
   * Tìm kiếm sản phẩm theo từ khoá.
   * @param q
   * @return
   */
  @GetMapping("/search")
  public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
    List<Product> products = productService.searchProducts(q);
    return ResponseEntity.ok(products);
  }

  /**
   * Tìm kiếm nâng cao với nhiều tiêu chí.
   * @param q
   * @param category
   * @param minPrice
   * @param maxPrice
   * @return
   */
  @GetMapping("/search/advanced")
  public ResponseEntity<List<Product>> advancedSearch(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice) {

    List<Product> products = productService.advancedSearch(q, category, minPrice, maxPrice);
    return ResponseEntity.ok(products);
  }

  /**
   * Tìm kiếm sản phẩm với phân trang.
   * @param q
   * @param category
   * @param minPrice
   * @param maxPrice
   * @param page
   * @param size
   * @return
   */
  @GetMapping("/search/paginated")
  public ResponseEntity<Page<Product>> searchWithPagination(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productService.searchProductsWithPagination(
        q, category, minPrice, maxPrice, pageable);

    return ResponseEntity.ok(products);
  }

  /**
   * Lấy gợi ý tự động cho ô tìm kiếm.
   * @param prefix
   * @return
   */
  @GetMapping("/autocomplete")
  public ResponseEntity<List<String>> autocomplete(@RequestParam String prefix) {
    List<String> suggestions = productService.getAutocompleteSuggestions(prefix);
    return ResponseEntity.ok(suggestions);
  }

  /**
   * Lấy thống kê phân loại sản phẩm.
   * @return
   */
  @GetMapping("/aggregations/category")
  public ResponseEntity<Map<String, Long>>
  getCategoryAggregation() {
    Map<String, Long> aggregation = productService.getCategoryAggregation();
    return ResponseEntity.ok(aggregation);
  }

  // Sync Management Endpoints

  /**
   * Đồng bộ dữ liệu từ PostgreSQL sang Elasticsearch.
   * Cung cấp các endpoint để thực hiện đồng bộ hoá toàn bộ hoặc tăng dần.
   */
  @PostMapping("/sync/full")
  public ResponseEntity<String> triggerFullSync() {
    try {
      elasticsearchSyncService.fullSync();
      return ResponseEntity.ok("Full synchronization completed successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Full synchronization failed: " + e.getMessage());
    }
  }

  /**
   * Đồng bộ dữ liệu tăng dần từ PostgreSQL sang Elasticsearch.
   * Cho phép chỉ đồng bộ những sản phẩm đã thay đổi kể từ lần đồng bộ cuối cùng.
   * @param since Thời gian bắt đầu đồng bộ, nếu không cung cấp sẽ lấy thời gian hiện tại trừ đi 1 giờ.
   */
  @PostMapping("/sync/incremental")
  public ResponseEntity<String> triggerIncrementalSync(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime since) {
    try {
      LocalDateTime syncTime = since != null ? since : LocalDateTime.now().minusHours(1);
      elasticsearchSyncService.incrementalSync(syncTime);
      return ResponseEntity.ok("Incremental synchronization completed successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Incremental synchronization failed: " + e.getMessage());
    }
  }

  /**
   * Đồng bộ một sản phẩm cụ thể từ PostgreSQL sang Elasticsearch.
   * @param id ID của sản phẩm cần đồng bộ.
   */
  @GetMapping("/sync/status")
  public ResponseEntity<SyncStatus> getSyncStatus() {
    SyncStatus status = elasticsearchSyncService.getSyncStatus();
    return ResponseEntity.ok(status);
  }

  // Utility endpoints

  /**
   * Tạo dữ liệu mẫu cho sản phẩm.
   * @return
   */
  @PostMapping("/sample-data")
  public ResponseEntity<String> createSampleData() {
    List<Product> sampleProducts = Arrays.asList(
        new Product("iPhone 15 Pro", "Latest iPhone with titanium design and A17 Pro chip",
            new BigDecimal("1199.99"), "Electronics"),
        new Product("Samsung Galaxy S24 Ultra", "Premium Android smartphone with S Pen",
            new BigDecimal("1299.99"), "Electronics"),
        new Product("MacBook Pro 14-inch", "Professional laptop with M3 chip",
            new BigDecimal("1999.99"), "Computers"),
        new Product("Dell XPS 15", "High-performance laptop for creators",
            new BigDecimal("1799.99"), "Computers"),
        new Product("Sony WH-1000XM5", "Industry-leading noise canceling headphones",
            new BigDecimal("399.99"), "Audio"),
        new Product("AirPods Pro 2nd Gen", "Wireless earbuds with adaptive transparency",
            new BigDecimal("249.99"), "Audio"),
        new Product("Nike Air Max 90", "Classic sneaker with Max Air cushioning",
            new BigDecimal("129.99"), "Footwear"),
        new Product("Adidas Ultraboost 22", "Energy-returning running shoes",
            new BigDecimal("189.99"), "Footwear"),
        new Product("Dyson V15 Detect", "Cordless vacuum with laser dust detection",
            new BigDecimal("749.99"), "Home Appliances"),
        new Product("Instant Pot Duo 7-in-1", "Multi-functional pressure cooker",
            new BigDecimal("99.99"), "Home Appliances")
    );

    productService.createProducts(sampleProducts);
    return ResponseEntity.ok("Sample data created successfully");
  }

  /**
   * Kiểm tra tình trạng sức khoẻ của hệ thống.
   * @return
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> health = new HashMap<>();

    try {
      // Check PostgreSQL
      long postgresCount = productService.findAllProducts().size();
      health.put("postgresql", Map.of(
          "status", "UP",
          "productCount", postgresCount
      ));
    } catch (Exception e) {
      health.put("postgresql", Map.of(
          "status", "DOWN",
          "error", e.getMessage()
      ));
    }

    try {
      // Check Elasticsearch
      SyncStatus syncStatus = elasticsearchSyncService.getSyncStatus();
      health.put("elasticsearch", Map.of(
          "status", "UP",
          "productCount", syncStatus.getElasticCount(),
          "inSync", syncStatus.isSync()
      ));
    } catch (Exception e) {
      health.put("elasticsearch", Map.of(
          "status", "DOWN",
          "error", e.getMessage()
      ));
    }

    return ResponseEntity.ok(health);
  }
}
