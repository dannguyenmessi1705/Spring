package com.didan.reactive.learn.r2dbc;

import com.didan.reactive.learn.r2dbc.repository.CustomerOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@Slf4j
public class CustomerOrderRepositoryTest extends AbstractTest {

  @Autowired
  private CustomerOrderRepository customerOrderRepository;

  // Giả lập test cho phương thức getProductsOrderByCustomer của CustomerOrderRepository
  @Test
  public void productsOrderByCutomer() {
    customerOrderRepository.getProductsOrderByCustomer("mike")
        .doOnNext(p -> log.info("{}", p)) // Ghi log thông tin của từng sản phẩm được truy xuất khi stream được phát (thay vì đợi tất cả hoàn thành mới in ra)
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .expectNextCount(2) // Mong đợi có đúng 2 sản phẩm được trả về
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify();
  }

  @Test
  public void orderDetailsByProduct() {
    customerOrderRepository.getOrderDetailsOrderByProduct("iphone 20")
        .doOnNext(p -> log.info("{}", p)) // Ghi log thông tin của từng chi tiết đơn hàng được truy xuất khi stream được phát (thay vì đợi tất cả hoàn thành mới in ra)
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .assertNext(od -> Assertions.assertEquals(975, od.amount())) // Kiểm tra chi tiết đơn hàng đầu tiên có amount là 975
        .assertNext(od -> Assertions.assertEquals(950, od.amount())) // Kiểm tra chi tiết đơn hàng thứ hai có amount là 950
        .expectComplete()// Mong đợi luồng hoàn thành mà không có lỗi
        .verify(); // Bắt đầu quá trình kiểm tra
  }
}
