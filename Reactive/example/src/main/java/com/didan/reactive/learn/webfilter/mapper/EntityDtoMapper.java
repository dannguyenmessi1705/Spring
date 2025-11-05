package com.didan.reactive.learn.webfilter.mapper;

import com.didan.reactive.learn.r2dbc.entity.Customer;
import com.didan.reactive.learn.webfilter.dto.CustomerDto;

public class EntityDtoMapper {

  public static Customer toEntity(CustomerDto customerDto) {
    var customer = new Customer();
    customer.setId(customerDto.id());
    customer.setName(customerDto.name());
    customer.setEmail(customerDto.email());
    return customer;
  }

  public static CustomerDto toDto(Customer customer) {
    return new CustomerDto(
        customer.getId(),
        customer.getName(),
        customer.getEmail()
    );
  }

}
