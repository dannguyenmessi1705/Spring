package com.didan.reactive.learn.r2dbc.repository;

import com.didan.reactive.learn.r2dbc.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
  // Trong R2DBC, chúng ta sử dụng ReactiveCrudRepository để hỗ trợ các thao tác CRUD phi chặn (non-blocking)
  // Các phương thức CRUD cơ bản như save, findById, findAll, deleteById đã được định nghĩa sẵn trong ReactiveCrudRepository

  Flux<Customer> findCustomerByName(String name);
}
