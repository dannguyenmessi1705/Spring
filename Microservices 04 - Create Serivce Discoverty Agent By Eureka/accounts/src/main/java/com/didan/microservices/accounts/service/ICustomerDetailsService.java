package com.didan.microservices.accounts.service;

import com.didan.microservices.accounts.dto.CustomerDetailsDto;

public interface ICustomerDetailsService {
  public CustomerDetailsDto fetchCustomerDetails(String mobileNumber);
}
