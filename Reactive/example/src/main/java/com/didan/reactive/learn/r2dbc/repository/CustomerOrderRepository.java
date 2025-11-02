package com.didan.reactive.learn.r2dbc.repository;

import com.didan.reactive.learn.r2dbc.dto.OrderDetails;
import com.didan.reactive.learn.r2dbc.entity.CustomerOrder;
import com.didan.reactive.learn.r2dbc.entity.Product;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {
  // Trong R2DBC, chúng ta sử dụng ReactiveCrudRepository để hỗ trợ các thao tác CRUD phi chặn (non-blocking)
  // Các phương thức CRUD cơ bản như save, findById, findAll, deleteById đã được định nghĩa sẵn trong ReactiveCrudRepository

  // Sử dụng @Query nếu cần định nghĩa các truy vấn tùy chỉnh
  @Query("""
      SELECT
          p.*
      FROM
          customer c
      INNER JOIN customer_order co ON c.id = co.customer_id
      INNER JOIN product p ON co.product_id = p.id
      WHERE
          c.name = :name
      """)
  Flux<Product> getProductsOrderByCustomer(String name);

  @Query("""
      SELECT
          co.order_id,
          c.name AS customer_name,
          p.description AS product_name,
          co.amount,
          co.order_date
      FROM
          customer c
      INNER JOIN customer_order co ON c.id = co.customer_id
      INNER JOIN product p ON p.id = co.product_id
      WHERE
          p.description = :description
      ORDER BY co.amount DESC""")
  Flux<OrderDetails> getOrderDetailsOrderByProduct(String description);
}
