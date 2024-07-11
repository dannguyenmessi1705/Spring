package com.didan.microservices.accounts.service.impl;

import com.didan.microservices.accounts.dto.AccountsDto;
import com.didan.microservices.accounts.dto.CardsDto;
import com.didan.microservices.accounts.dto.CustomerDetailsDto;
import com.didan.microservices.accounts.dto.LoansDto;
import com.didan.microservices.accounts.entity.Accounts;
import com.didan.microservices.accounts.entity.Customer;
import com.didan.microservices.accounts.exception.ResourceNotFoundException;
import com.didan.microservices.accounts.mapper.AccountsMapper;
import com.didan.microservices.accounts.mapper.CustomerMapper;
import com.didan.microservices.accounts.repository.AccountsRepository;
import com.didan.microservices.accounts.repository.CustomerRepository;
import com.didan.microservices.accounts.service.ICustomerDetailsService;
import com.didan.microservices.accounts.service.client.CardsFeignClient;
import com.didan.microservices.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CustomerDetailsService implements ICustomerDetailsService{
  private final CustomerRepository customerRepository;
  private final AccountsRepository accountsRepository;
  private final CardsFeignClient cardsFeignClient;
  private final LoansFeignClient loansFeignClient;
  @Override
  public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
    Customer customer = customerRepository.findByMobile(mobileNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
        .orElseThrow(() -> new ResourceNotFoundException("Accounts", "customerID", ""+customer.getCustomerId()));

    CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
    customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
    ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);
    ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
    customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody()); // Gọi đến Microservices loans để lấy thông tin khoản vay của khách hàng
    customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody()); // Gọi đến Microservices cards để lấy thông tin thẻ của khách hàng
    return customerDetailsDto;
  }
}
