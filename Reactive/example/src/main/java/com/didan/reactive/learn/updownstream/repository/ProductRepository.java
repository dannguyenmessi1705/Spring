package com.didan.reactive.learn.updownstream.repository;

import com.didan.reactive.learn.updownstream.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

}
