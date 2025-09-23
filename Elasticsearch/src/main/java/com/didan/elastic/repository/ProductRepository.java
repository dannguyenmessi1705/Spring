package com.didan.elastic.repository;

import com.didan.archetype.aop.annotation.TimeTraceAspect;
import com.didan.elastic.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author dannd1
 * @since 7/2/2025
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  // Các query methods cho PostgreSQL
  @TimeTraceAspect
  List<Product> findByAvailableTrue();

  @TimeTraceAspect
  List<Product> findByCategory(String category);

  @TimeTraceAspect
  @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
  List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice);

  // Cho việc đồng bộ dữ liệu
  @TimeTraceAspect
  @Query("SELECT p FROM Product p WHERE p.updatedDate > :lastSyncTime ORDER BY p.updatedDate ASC")
  List<Product> findUpdatedSince(@Param("lastSyncTime") LocalDateTime lastSyncTime);

  @TimeTraceAspect
  @Query("SELECT COUNT(p) FROM Product p WHERE p.updatedDate > :lastSyncTime")
  long countUpdatedSince(@Param("lastSyncTime") LocalDateTime lastSyncTime);
}
