package com.didan.reactive.learn.r2dbc;

import com.didan.reactive.learn.r2dbc.dto.OrderDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

@Slf4j
class DatabaseClientTest extends AbstractTest {

  // Sử dụng DatabaseClient để thực hiện truy vấn tùy chỉnh mà không cần dùng @Repository
  @Autowired
  private DatabaseClient databaseClient; // Inject DatabaseClient để thực hiện truy vấn tùy chỉnh

  @Test
  void orderDetailsByProduct2() {
    var query = """
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
        ORDER BY co.amount DESC
        """;
    databaseClient.sql(query) // Thực hiện truy vấn SQL tùy chỉnh
        .bind("description", "iphone 20") // Gán giá trị cho tham số :description trong truy vấn
        .mapProperties(OrderDetails.class) // Ánh xạ kết quả truy vấn vào lớp OrderDetails
        .all()
        .doOnNext(dto -> log.info("{}", dto))
        .as(StepVerifier::create)
        .assertNext(dto -> Assertions.assertEquals(975, dto.amount()))
        .assertNext(dto -> Assertions.assertEquals(950, dto.amount()))
        .expectComplete()
        .verify();


  }
}
