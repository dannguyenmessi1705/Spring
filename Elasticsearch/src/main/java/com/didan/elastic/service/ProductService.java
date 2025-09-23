package com.didan.elastic.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.MultiBucketBase;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import com.didan.elastic.entity.Product;
import com.didan.elastic.entity.ProductDocument;
import com.didan.elastic.repository.ProductDocumentRepository;
import com.didan.elastic.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dannd1
 * @since 7/4/2025
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;
  private final ElasticsearchSyncService elasticsearchSyncService;
  private final ElasticsearchTemplate elasticsearchTemplate;

  /**
   * Tạo mới một sản phẩm và đồng bộ với Elasticsearch.
   * @param product
   * @return
   */
  public Product createProduct(Product product) {
    try {
      Product savedProduct = productRepository.save(product);

      // Đồng bộ dữ liệu với Elasticsearch
      elasticsearchSyncService.syncProduct(product.getId());
      log.info("Product with ID {} created and synchronized to Elasticsearch", savedProduct.getId());
      return  savedProduct;
    } catch (Exception e) {
      log.error("Failed to create product and synchronize with Elasticsearch", e);
      throw new RuntimeException("Failed to create product", e);
    }
  }

  /**
   * Cập nhật thông tin sản phẩm và đồng bộ với Elasticsearch.
   * @param id
   * @param product
   * @return
   */
  public Product updateProduct(Long id, Product product) {
    try {
      Optional<Product> foundProduct = productRepository.findById(id);

      if (foundProduct.isEmpty()) {
        throw new RuntimeException("Product with ID " + id + " not found");
      }

      Product existingProduct = foundProduct.get();

      existingProduct.setName(product.getName());
      existingProduct.setDescription(product.getDescription());
      existingProduct.setPrice(product.getPrice());
      existingProduct.setCategory(product.getCategory());
      existingProduct.setAvailable(product.getAvailable());

      Product updatedProduct = productRepository.save(existingProduct);

      // Đồng bộ dữ liệu với Elasticsearch
      elasticsearchSyncService.syncProduct(updatedProduct.getId());

      log.info("Product with ID {} updated and synchronized to Elasticsearch", updatedProduct.getId());
      return updatedProduct;
    } catch (Exception e) {
      log.error("Failed to update product and synchronize with Elasticsearch", e);
      throw new RuntimeException("Failed to update product", e);
    }
  }

  /**
   * Xoá sản phẩm và đồng bộ hoá với Elasticsearch.
   * @param id
   */
  public void deleteProduct(Long id) {
    try {
      if (!productRepository.existsById(id)) {
        throw new RuntimeException("Product with ID " + id + " not found");
      }

      productRepository.deleteById(id);
      // Xoá dữ liệu khỏi Elasticsearch
      elasticsearchSyncService.deleteProductDocument(id);
      log.info("Product with ID {} deleted", id);
    } catch (Exception e) {
      log.error("Failed to delete product and synchronize with Elasticsearch", e);
      throw new RuntimeException("Failed to delete product", e);
    }
  }

  // Lấy sản phẩm từ PostgreSQL (cho data chính xác)
  /**
   * Tìm kiếm sản phẩm theo ID.
   * @param id
   * @return
   */
  public Optional<Product> findById(Long id) {
    try {
      return productRepository.findById(id);
    } catch (Exception e) {
      log.error("Failed to find product by ID {}", id, e);
      throw new RuntimeException("Failed to find product", e);
    }
  }

  /**
   * Tìm tất cả sản phẩm.
   * @return
   */
  public List<Product> findAllProducts() {
    try {
      return productRepository.findAll();
    } catch (Exception e) {
      log.error("Failed to find all products", e);
      throw new RuntimeException("Failed to find all products", e);
    }
  }

  /**
   * Tìm tất cả sản phẩm với phân trang.
   * @param pageable
   * @return
   */
  public Page<Product> findAllProducts(Pageable pageable) {
    try {
      return productRepository.findAll(pageable);
    } catch (Exception e) {
      log.error("Failed to find all products with pagination", e);
      throw new RuntimeException("Failed to find all products with pagination", e);
    }
  }

  // Lấy sản phẩm từ Elasticsearch (cho hiệu suất cao hơn)

  /**
   * Tìm kiếm sản phẩm theo tên hoặc mô tả với thuật toán fuzzy.
   * @param searchTerm
   * @return
   */
  public List<Product> searchProducts(String searchTerm) {
    try {
      List<ProductDocument> documents = productDocumentRepository.findByNameOrDescriptionFuzzy(searchTerm);

      return documents.stream()
          .map(ProductDocument::toProduct)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to search products by term {}", searchTerm, e);
      return fallbackSearch(searchTerm); // Fallback vào PostgreSQL nếu Elasticsearch không thành công
    }
  }

  /**
   * Tìm kiếm sản phẩm theo danh mục.
   * @param searchTerm
   * @param category
   * @param minPrice
   * @param maxPrice
   * @return
   */
  public List<Product> advancedSearch(String searchTerm, String category,
      BigDecimal minPrice, BigDecimal maxPrice) {
    try {
      Double minPriceDouble = minPrice != null ? minPrice.doubleValue() : null;
      Double maxPriceDouble = maxPrice != null ? maxPrice.doubleValue() : null;

      List<ProductDocument> documents;

      if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        documents = productDocumentRepository
            .findBySearchTermAndCategoryAndPriceRange(
                searchTerm, category, minPriceDouble, maxPriceDouble);
      } else {
        // Tạo query phức tạp hơn
        documents = performComplexSearch(category, minPriceDouble, maxPriceDouble);
      }

      return documents.stream()
          .map(ProductDocument::toProduct)
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Error in advanced search", e);
      // Fallback to PostgreSQL
      return fallbackAdvancedSearch(searchTerm, category, minPrice, maxPrice);
    }
  }

  /**
   * Tìm kiếm sản phẩm với phân trang và sắp xếp.
   * @param searchTerm
   * @param category
   * @param minPrice
   * @param maxPrice
   * @param pageable
   * @return
   */
  public Page<Product> searchProductsWithPagination(String searchTerm,
      String category,
      BigDecimal minPrice, BigDecimal maxPrice,
      Pageable pageable) {
    try {
      // Build the bool query using the modern Elasticsearch client
      BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

      // Add search term with fuzzy matching
      if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        Query multiMatchQuery = QueryBuilders.multiMatch()
            .query(searchTerm)
            .fields("name", "description")
            .fuzziness("AUTO")
            .build()._toQuery();
        boolQueryBuilder.must(multiMatchQuery);
      }

      // Add category filter
      if (category != null && !category.trim().isEmpty()) {
        Query termQuery = QueryBuilders.term()
            .field("category")
            .value(category)
            .build()._toQuery();
        boolQueryBuilder.must(termQuery);
      }

      // Add price range filter
      if (minPrice != null || maxPrice != null) {
        RangeQuery.Builder rangeBuilder = QueryBuilders.range();
        if (minPrice != null) {
          rangeBuilder.term(t ->
              t.field("price")
                  .gte(String.valueOf(minPrice.doubleValue())));
        }
        if (maxPrice != null) {
          rangeBuilder.term(t ->
              t.field("price")
                  .gte(String.valueOf(maxPrice.doubleValue())));
        }
        boolQueryBuilder.must(rangeBuilder.build()._toQuery());
      }

      boolQueryBuilder.must(m ->
          m.term(t ->
              t.field("available").value(true)));

      NativeQueryBuilder searchQuery = new NativeQueryBuilder()
          .withQuery(boolQueryBuilder.build()._toQuery())
          .withPageable(pageable)
          .withSort(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc))))
          .withSort(SortOptions.of(s -> s.field(f -> f.field("createdDate").order(SortOrder.Desc))));

      SearchHits<ProductDocument> searchHits = elasticsearchTemplate
          .search(searchQuery.build(), ProductDocument.class);

      List<Product> products = searchHits.stream()
          .map(hit -> hit.getContent().toProduct())
          .collect(Collectors.toList());

      return new PageImpl<>(products, pageable, searchHits.getTotalHits());

    } catch (Exception e) {
      log.error("Error in paginated search", e);
      // Fallback to PostgreSQL
      return fallbackPaginatedSearch(searchTerm, category, minPrice, maxPrice, pageable);
    }
  }

  /**
   * Lấy gợi ý tự động hoàn thành dựa trên tiền tố.
   * @param prefix
   * @return
   */
  public List<String> getAutocompleteSuggestions(String prefix) {
    try {
      List<ProductDocument> documents = productDocumentRepository
          .findByNameStartingWithOrContaining(prefix.toLowerCase());

      return documents.stream()
          .map(ProductDocument::getName)
          .distinct()
          .limit(10)
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Error getting autocomplete suggestions", e);
      return Collections.emptyList();
    }
  }

  /**
   * Lấy thống kê phân loại sản phẩm từ Elasticsearch.
   * @return
   */
  public Map<String, Long> getCategoryAggregation() {
    try {
      // Build query using modern Elasticsearch client
      Query termQuery = QueryBuilders.term()
          .field("available")
          .value(true)
          .build()._toQuery();

      // Build the native query with aggregation using the correct API
      NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
          .withQuery(termQuery)
          .withMaxResults(0); // Only get aggregations, no documents

      // Add aggregation manually using the builder pattern
      queryBuilder.withAggregation("category_agg",
          AggregationBuilders.terms(builder -> builder
              .field("category")
              .size(100) // Limit number of categories returned
          )
      );

      SearchHits<ProductDocument> searchHits = elasticsearchTemplate
          .search(queryBuilder.build(), ProductDocument.class);

      Map<String, Long> categoryCounts = Objects.requireNonNull(((ElasticsearchAggregations) Objects.requireNonNull(searchHits
              .getAggregations()))
              .get("category_agg"))
          .aggregation()
          .getAggregate()
          .sterms()
          .buckets()
          .array()
          .stream()
          .collect(Collectors.toMap(t -> t.key()._toJsonString(), MultiBucketBase::docCount));
      // Process aggregation results

      log.info("Category count: {}", categoryCounts.size());
      return categoryCounts;

    } catch (Exception e) {
      log.error("Error getting category aggregation", e);
      // Fallback to PostgreSQL aggregation
      return fallbackCategoryAggregation();
    }
  }

  /**
   * Fallback method to get category aggregation from PostgreSQL.
   * @return
   */
  private Map<String, Long> fallbackCategoryAggregation() {
    log.warn("Elasticsearch aggregation failed, falling back to PostgreSQL");
    try {
      List<Product> products = productRepository.findAll();
      return products.stream()
          .filter(Product::getAvailable)
          .collect(Collectors.groupingBy(
              Product::getCategory,
              Collectors.counting()
          ));
    } catch (Exception e) {
      log.error("Error in fallback category aggregation", e);
      return new HashMap<>();
    }
  }

  // Fallback methods khi Elasticsearch không available

  /**
   * Fallback tìm kiếm sản phẩm theo tên hoặc mô tả.
   * @param searchTerm
   * @return
   */
  private List<Product> fallbackSearch(String searchTerm) {
    // Simple fallback search using PostgreSQL
    return productRepository.findAll().stream()
        .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
            p.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
        .collect(Collectors.toList());
  }

  /**
   * Fallback tìm kiếm nâng cao theo các tiêu chí.
   * @param searchTerm
   * @param category
   * @param minPrice
   * @param maxPrice
   * @return
   */
  private List<Product> fallbackAdvancedSearch(String searchTerm, String category,
      BigDecimal minPrice, BigDecimal maxPrice) {
    List<Product> products = productRepository.findAll();

    return products.stream()
        .filter(p -> {
          // Search term filter
          if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String term = searchTerm.toLowerCase();
            if (!p.getName().toLowerCase().contains(term) &&
                !p.getDescription().toLowerCase().contains(term)) {
              return false;
            }
          }

          // Category filter
          if (category != null && !category.trim().isEmpty()) {
            if (!p.getCategory().equals(category)) {
              return false;
            }
          }

          // Price range filter
          if (minPrice != null && p.getPrice().compareTo(minPrice) < 0) {
            return false;
          }
          if (maxPrice != null && p.getPrice().compareTo(maxPrice) > 0) {
            return false;
          }

          return p.getAvailable();
        })
        .collect(Collectors.toList());
  }

  /**
   * Fallback tìm kiếm sản phẩm với phân trang.
   * @param searchTerm
   * @param category
   * @param minPrice
   * @param maxPrice
   * @param pageable
   * @return
   */
  private Page<Product> fallbackPaginatedSearch(String searchTerm, String category,
      BigDecimal minPrice, BigDecimal maxPrice,
      Pageable pageable) {
    List<Product> filteredProducts = fallbackAdvancedSearch(searchTerm, category, minPrice, maxPrice);

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), filteredProducts.size());

    List<Product> pageContent = filteredProducts.subList(start, end);

    return new PageImpl<>(pageContent, pageable, filteredProducts.size());
  }

  /**
   * Thực hiện tìm kiếm phức tạp với các tiêu chí như danh mục, giá tối thiểu và tối đa.
   * @param category
   * @param minPrice
   * @param maxPrice
   * @return
   */
  private List<ProductDocument> performComplexSearch(String category, Double minPrice, Double maxPrice) {

    BoolQuery.Builder boolQuery = QueryBuilders.bool();

    if (category != null && !category.trim().isEmpty()) {
      boolQuery.must(m -> m.term(t -> t.field("category").value(category)));
    }

    if (minPrice != null || maxPrice != null) {
      RangeQuery.Builder rangeQuery = QueryBuilders.range();

      if (minPrice != null) rangeQuery.term(t -> t.field("price").gte(String.valueOf(minPrice)));
      if (maxPrice != null) rangeQuery.term(t -> t.field("price").lte(String.valueOf(maxPrice)));
      boolQuery.must(rangeQuery.build()._toQuery());
    }

    boolQuery.must(m ->
        m.term(t ->
            t.field("available").value(true)));

    NativeQueryBuilder searchQuery = new NativeQueryBuilder()
        .withQuery(boolQuery.build()._toQuery())
        .withSort(SortOptions.of(s -> s.field(f -> f.field("createdDate").order(SortOrder.Desc))));

    SearchHits<ProductDocument> searchHits = elasticsearchTemplate.search(searchQuery.build(), ProductDocument.class);

    return searchHits.stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList());
  }

  // Bulk operations

  /**
   * Tạo mới nhiều sản phẩm cùng lúc và đồng bộ với Elasticsearch.
   * @param products
   * @return
   */
  public List<Product> createProducts(List<Product> products) {
    try {
      List<Product> savedProducts = productRepository.saveAll(products);

      // Async sync to Elasticsearch
      CompletableFuture.runAsync(() -> savedProducts.forEach(product -> {
        try {
          elasticsearchSyncService.syncProduct(product.getId());
        } catch (Exception e) {
          log.error("Error syncing product {} during bulk create", product.getId(), e);
        }
      }));

      return savedProducts;

    } catch (Exception e) {
      log.error("Error creating products in bulk", e);
      throw new RuntimeException("Failed to create products", e);
    }
  }

}
