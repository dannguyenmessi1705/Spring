package com.didan.reactive.learn.r2dbc;

import com.didan.reactive.learn.r2dbc.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

@Slf4j
public class ProductRepositoryTest extends AbstractTest {

  @Autowired
  private ProductRepository productRepository;

  // Giả lập test cho phương thức query sort của ProductRepository
  @Test
  public void pageable() {
    productRepository.findBy(PageRequest.of(0, 3).withSort(Sort.by("price").ascending()))
        .doOnNext(p -> log.info("{}", p)) // Ghi log thông tin của từng sản phẩm được truy xuất khi stream được phát (thay vì đợi tất cả hoàn thành mới in ra)
        .as(StepVerifier::create)
        .assertNext(p -> Assertions.assertEquals(200, p.getPrice())) // Kiểm tra sản phẩm đầu tiên có giá là 200
        .assertNext(p -> Assertions.assertEquals(250, p.getPrice())) // Kiểm tra sản phẩm thứ hai có giá là 250
        .assertNext(p -> Assertions.assertEquals(300, p.getPrice())) // Kiểm tra sản phẩm thứ ba có giá là 300
        .expectComplete()
        .verify();
  }
}
