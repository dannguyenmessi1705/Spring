package com.didan.reactive.learn.updownstream.service;

import com.didan.reactive.learn.updownstream.dto.ProductDto;
import com.didan.reactive.learn.updownstream.mapper.EntityDtoMapper;
import com.didan.reactive.learn.updownstream.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final Sinks.Many<ProductDto> sink;

  public Flux<ProductDto> saveProducts(Flux<ProductDto> flux) {
    return flux.map(EntityDtoMapper::toEntity)
        .as(productRepository::saveAll)
        .map(EntityDtoMapper::toDto);

  }

  public Mono<Long> getProductCount() {
    return productRepository.count();
  }

  public Flux<ProductDto> getAllProducts() {
    return productRepository.findAll()
        .map(EntityDtoMapper::toDto);
  }

  public Mono<ProductDto> saveProduct(Mono<ProductDto> mono) {
    return mono.map(EntityDtoMapper::toEntity)
        .flatMap(this.productRepository::save)
        .map(EntityDtoMapper::toDto)
        .doOnNext(this.sink::tryEmitNext);
  }

  public Flux<ProductDto> productStream() {
    return this.sink.asFlux(); // Chuyển Sinks.Many thành Flux để phát luồng dữ liệu ra bên ngoài
  }

}
