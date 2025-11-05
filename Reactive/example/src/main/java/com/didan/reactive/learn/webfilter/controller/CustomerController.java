package com.didan.reactive.learn.webfilter.controller;

import com.didan.reactive.learn.webfilter.dto.CustomerDto;
import com.didan.reactive.learn.webfilter.exception.ApplicationException;
import com.didan.reactive.learn.webfilter.filter.Category;
import com.didan.reactive.learn.webfilter.service.CustomerService;
import com.didan.reactive.learn.webfilter.validator.RequestValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public Flux<CustomerDto> allCustomers(@RequestAttribute("category") Category category) {
    log.info("Category from WebFilter: {}", category); // In ra category lấy từ WebFilter để kiểm tra
    return customerService.getAllCustomers();
  }

  @GetMapping("paginated")
  public Mono<List<CustomerDto>> paginatedCustomers(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "3") Integer size) {
    return customerService.getAllCustomers(page, size);
  }

  @GetMapping("{id}")
  public Mono<CustomerDto> getCustomerById(@PathVariable Integer id) {
    return customerService.getCustomerById(id)
        .switchIfEmpty(ApplicationException.customerNotFound(id)); // Nếu không tìm thấy khách hàng, trả về lỗi CustomerNotFoundException
  }

  @PostMapping
  public Mono<CustomerDto> createCustomer(@RequestBody Mono<CustomerDto> customerDto) {
    return customerDto.transform(RequestValidator.validate()) // Áp dụng bộ validator để kiểm tra dữ liệu đầu vào
        .as(customerService::saveCustomer); // Lưu khách hàng nếu dữ liệu hợp lệ
  }

  @PutMapping("{id}")
  public Mono<CustomerDto> updateCustomer(@PathVariable Integer id,
      @RequestBody Mono<CustomerDto> customerDto) {
    return customerDto.transform(RequestValidator.validate()) // transform để áp dụng bộ validator kiểm tra dữ liệu đầu vào
        .as(validRequest -> customerService.updateCustomer(id, validRequest)) // Cập nhật khách hàng nếu dữ liệu hợp lệ, dua vào id và dữ liệu đã được kiểm tra
        .switchIfEmpty(ApplicationException.customerNotFound(id)); // Nếu không tìm thấy khách hàng để cập nhật, trả về lỗi CustomerNotFoundException
  }

  @DeleteMapping("{id}")
  public Mono<Void> deleteCustomer(@PathVariable Integer id) {
    return customerService.deleteCustomer(id) // Xóa khách hàng theo id
        .filter(b -> b) // Lọc kết quả trả về, chỉ tiếp tục nếu khách hàng đã được xóa (b == true)
        .switchIfEmpty(ApplicationException.customerNotFound(id)) // Nếu không tìm thấy khách hàng để xóa, trả về lỗi CustomerNotFoundException
        .then(); // Trả về Mono<Void> vi ham deleteCustomer dang tra ve Mono<Boolean>
  }

}
