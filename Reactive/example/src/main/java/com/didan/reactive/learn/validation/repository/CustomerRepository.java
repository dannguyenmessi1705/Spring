package com.didan.reactive.learn.validation.repository;

import com.didan.reactive.learn.r2dbc.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
  // Trong R2DBC, chúng ta sử dụng ReactiveCrudRepository để hỗ trợ các thao tác CRUD phi chặn (non-blocking)
  // Các phương thức CRUD cơ bản như save, findById, findAll, deleteById đã được định nghĩa sẵn trong ReactiveCrudRepository

  Flux<Customer> findCustomerByName(String name);

  @Modifying // Chú thích @Modifying để chỉ định rằng phương thức này sẽ thực hiện thao tác thay đổi dữ liệu
  @Query("DELETE FROM customer WHERE id = :id")
  Mono<Boolean> deleteCustomersById(Integer id);

  Flux<Customer> findBy(Pageable pageable);
}
