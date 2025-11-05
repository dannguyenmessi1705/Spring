package com.didan.reactive.learn.validation.service;

import com.didan.reactive.learn.validation.dto.CustomerDto;
import com.didan.reactive.learn.validation.mapper.EntityDtoMapper;
import com.didan.reactive.learn.validation.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  public Flux<CustomerDto> getAllCustomers() {
    return this.customerRepository.findAll() // Lấy tất cả khách hàng từ repository
        .map(EntityDtoMapper::toDto); // Chuyển đổi từng Customer entity thành CustomerDto
  }

  public Mono<List<CustomerDto>> getAllCustomers(Integer page, Integer size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    return customerRepository.findBy(pageRequest)
        .map(EntityDtoMapper::toDto)
        .collectList(); // Thu thập tất cả CustomerDto thành một List và trả về Mono<List<CustomerDto>>
//        .zipWith(customerRepository.count()) // Kết hợp với Mono<Long> trả về tổng số khách hàng
//        .map(c -> new PageImpl<>(c.getT1(), pageRequest, c.getT2())); // Tạo Page<CustomerDto> từ List<CustomerDto> và tổng số khách hàng
  }

  public Mono<CustomerDto> getCustomerById(Integer id) {
    return this.customerRepository.findById(id) // Tìm khách hàng theo ID
        .map(EntityDtoMapper::toDto); // Chuyển đổi Customer entity thành CustomerDto
  }

  public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> customerDtoMono) { // Truyền vào Mono<CustomerDto>
    return customerDtoMono.map(EntityDtoMapper::toEntity) // Chuyển đổi CustomerDto thành Customer entity
        .flatMap(this.customerRepository::save) // Sau khi lưu có kiểu Mono<Customer> nên phải dùng flatMap để tránh lồng Mono
        .map(EntityDtoMapper::toDto);
  }

  public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> customerDtoMono) {
    return this.customerRepository.findById(id) // Tìm khách hàng theo ID
        .flatMap(entity -> customerDtoMono) // Lấy dữ liệu mới từ client (Mono<CustomerDto>) dùng flatMap để tránh lồng Mono (biến đổi Mono sang Entity)
        .map(EntityDtoMapper::toEntity) // Chuyển đổi CustomerDto thành Customer entity
        .doOnNext(c -> c.setId(id)) // Đảm bảo ID không bị thay đổi
        .flatMap(customerRepository::save) // Lưu khách hàng đã được cập nhật (Mono<Customer>) dùng flatMap để tránh lồng Mono, vì save trả về Mono<Customer> biến đổi sang DTO
        .map(EntityDtoMapper::toDto); // Chuyển đổi Customer entity thành CustomerDto
  }

  public Mono<Boolean> deleteCustomer(Integer id) {
    return this.customerRepository.deleteCustomersById(id); // Xoá khách hàng theo ID
  }
}
