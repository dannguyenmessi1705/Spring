package com.didan.elastic.repository;

import com.didan.archetype.aop.annotation.TimeTraceAspect;
import com.didan.elastic.entity.ProductDocument;
import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dannd1
 * @since 7/3/2025
 */
@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {

  @TimeTraceAspect
  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^2\", \"description\"], \"fuzziness\": \"AUTO\"}}")
  List<ProductDocument> findByNameOrDescriptionFuzzy(String searchTerm);

  @TimeTraceAspect
  List<ProductDocument> findByCategory(String category);

  @TimeTraceAspect
  List<ProductDocument> findByPriceBetween(Double priceStart, Double priceEnd);

  @TimeTraceAspect
  List<ProductDocument> findByAvailableTrue();

  @TimeTraceAspect
  @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^2\", \"description\"]}}, {\"term\": {\"category\": \"?1\"}}, {\"range\": {\"price\": {\"gte\": ?2, \"lte\": ?3}}}]}}")
  List<ProductDocument> findBySearchTermAndCategoryAndPriceRange(String searchTerm, String category, Double minPrice, Double maxPrice);

  @TimeTraceAspect
  @Query("{\"bool\": {\"should\": [{\"prefix\": {\"name\": \"?0\"}}, {\"wildcard\": {\"name\": \"*?0*\"}}]}}")
  List<ProductDocument> findByNameStartingWithOrContaining(String prefix);
}
