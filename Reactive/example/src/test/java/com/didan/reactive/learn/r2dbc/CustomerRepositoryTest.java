package com.didan.reactive.learn.r2dbc;

import com.didan.reactive.learn.r2dbc.entity.Customer;
import com.didan.reactive.learn.r2dbc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@Slf4j
public class CustomerRepositoryTest extends AbstractTest {

  @Autowired
  private CustomerRepository customerRepository;

  // Giả lập test cho phương thức findAll của CustomerRepository
  @Test
  public void findAll() {
    customerRepository.findAll()
        .doOnNext(c -> log.info("{}", c)) // Ghi log thông tin của từng khách hàng được truy xuất khi stream được phát (thay vì đợi tất cả hoàn thành mới in ra)
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .expectNextCount(10) // Mong đợi có đúng 10 khách hàng được trả về
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify(); // Bắt đầu quá trình kiểm tra
  }

  @Test
  public void findById() {
    customerRepository.findById(2)
        .doOnNext(c -> log.info("{}", c)) // Ghi log thông tin của khách hàng được truy xuất
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .assertNext(c -> Assertions.assertEquals("mike", c.getName())) // Kiểm tra tên của khách hàng có ID 2 có đúng là "mike"
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify(); // Bắt đầu quá trình kiểm tra
  }

  @Test
  public void findCustomerByName() {
    customerRepository.findCustomerByName("jake")
        .doOnNext(c -> log.info("{}", c)) // Ghi log thông tin của khách hàng được truy xuất
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail())) // Kiểm tra email của khách hàng có tên "jake"
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify(); // Bắt đầu quá trình kiểm tra
  }

  @Test
  public void testInsertAndRemove() {
    var customer = new Customer();
    customer.setName("dan");
    customer.setEmail("dan@gmail.com");

    // Insert customer
    customerRepository.save(customer)
        .doOnNext(c -> log.info("{}", c))
        .as(StepVerifier::create)
        .assertNext(c -> Assertions.assertNotNull(c.getId()))
        .expectComplete()
        .verify();

    // count
    customerRepository.count() // Đếm số lượng khách hàng trong cơ sở dữ liệu
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .expectNext(11L) // Mong đợi số lượng khách hàng là 11 (10 ban đầu + 1 mới chèn)
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify();

    // Remove customer and count again
    customerRepository.deleteById(11) // Giả sử ID của khách hàng mới chèn là 11
        .then(customerRepository.count()) // Sau khi xóa, đếm lại số lượng khách hàng
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .expectNext(10L) // Mong đợi số lượng khách hàng trở lại 10
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify();
  }

  @Test
  public void updateCustomer() {
    customerRepository.findCustomerByName("ethan") // Tìm khách hàng có tên "ethan"
        .doOnNext(c -> c.setName("noel")) // Cập nhật tên của khách hàng thành "noel"
        .flatMap(c -> customerRepository.save(c)) // Lưu lại khách hàng đã được cập nhật (Sử dụng flatMap để ánh xạ luồng dữ liệu, tránh việc lồng Flux/Mono, không dùng map)
        .doOnNext(c -> log.info("{}", c)) // Ghi log thông tin của khách hàng đã được cập nhật
        .as(StepVerifier::create) // Sử dụng StepVerifier để kiểm tra luồng dữ liệu phản ứng
        .assertNext(c -> Assertions.assertEquals("noel", c.getName())) // Kiểm tra tên của khách hàng đã được cập nhật đúng thành "noel"
        .expectComplete() // Mong đợi luồng hoàn thành mà không có lỗi
        .verify();
  }
}
