package com.didan.reactive.learn.crud.controller;

import com.didan.reactive.learn.crud.dto.CustomerDto;
import com.didan.reactive.learn.crud.service.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public Flux<CustomerDto> allCustomers() {
    return customerService.getAllCustomers();
  }

  @GetMapping("paginated")
  public Mono<List<CustomerDto>> paginatedCustomers(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "3") Integer size) {
    return customerService.getAllCustomers(page, size);
  }

  @GetMapping("{id}")
  public Mono<ResponseEntity<CustomerDto>> getCustomerById(@PathVariable Integer id) {
    return customerService.getCustomerById(id)
        .map(ResponseEntity::ok) // Nếu tìm thấy khách hàng, trả về 200 OK với dữ liệu khách hàng
        .defaultIfEmpty(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy khách hàng
  }

  @PostMapping
  public Mono<CustomerDto> createCustomer(@RequestBody Mono<CustomerDto> customerDto) {
    return customerService.saveCustomer(customerDto);
  }

  @PutMapping("{id}")
  public Mono<ResponseEntity<CustomerDto>> updateCustomer(@PathVariable Integer id,
      @RequestBody Mono<CustomerDto> customerDto) {
    return customerService.updateCustomer(id, customerDto)
        .map(ResponseEntity::ok) // Nếu cập nhật thành công, trả về 200 OK với dữ liệu khách hàng đã cập nhật;
        .defaultIfEmpty(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy khách hàng để cập nhật
  }

  @DeleteMapping("{id}")
  public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {
    return customerService.deleteCustomer(id)
// Nếu kiểu dữ liệu trả về là Mono<Boolean> thì dùng cách này
        .filter(b -> true) // Lọc kết quả để kiểm tra xem có xoá thành công hay không
        .map(b -> ResponseEntity.ok().<Void>build()) // Trả về 200 OK nếu xoá thành công
        .defaultIfEmpty(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy khách hàng để xoá
// Nếu kiểu dữ liệu trả về là Mono<Void> thì dùng cách này
//        .then(Mono.fromCallable(() -> ResponseEntity.ok().<Void>build())) // Trả về 200 OK nếu xoá thành công
//        .defaultIfEmpty(ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy khách hàng để xoá
  }

}
