package com.didan.microservices.accounts.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.didan.microservices.accounts.constant.AccountsConstant;
import com.didan.microservices.accounts.dto.AccountsDto;
import com.didan.microservices.accounts.dto.CustomerDto;
import com.didan.microservices.accounts.entity.Accounts;
import com.didan.microservices.accounts.entity.Customer;
import com.didan.microservices.accounts.exception.CustomerAlreadyExistException;
import com.didan.microservices.accounts.exception.ResourceNotFoundException;
import com.didan.microservices.accounts.mapper.AccountsMapper;
import com.didan.microservices.accounts.mapper.CustomerMapper;
import com.didan.microservices.accounts.repository.AccountsRepository;
import com.didan.microservices.accounts.repository.CustomerRepository;
import com.didan.microservices.accounts.service.IAccountsService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountsService {
	private final AccountsRepository accountsRepository;
	private final CustomerRepository customerRepository;

	/**
	 * 
	 * @param customerDto - customerDto Object
	 */
	@Override
	public void createAccounts(CustomerDto customerDto) {
		Optional<Customer> findCustomer = customerRepository.findByMobile(customerDto.getMobile());
		if (findCustomer.isPresent()) {
			throw new CustomerAlreadyExistException(
					"Customer with mobile number " + customerDto.getMobile() + " already exist");
		}
		Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
		Customer savedCustomer = customerRepository.save(customer);
		accountsRepository.save(createAccountsFromCustomer(savedCustomer));
	}

	private Accounts createAccountsFromCustomer(Customer customer) {
		Accounts accounts = new Accounts();
		accounts.setCustomerId(customer.getCustomerId());
		accounts.setAccountType("Savings");
		accounts.setBranchAddress(AccountsConstant.ADDRESS);
		int randomAccNumber = 1000000 + new Random().nextInt(900000);
		accounts.setAccountNumber(randomAccNumber);
		return accounts;
	}

	/**
	 * @param mobile - số điện thoại khách hàng
	 * @return - trả về CustomerDto
	 */
	@Override
	public CustomerDto fetch(String mobile) {
		Customer customer = customerRepository.findByMobile(mobile)
				.orElseThrow(() -> new ResourceNotFoundException("Customer", "mobile", mobile));
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
				.orElseThrow(() -> new ResourceNotFoundException("Accounts", "customerID", ""+customer.getCustomerId()));
		
		CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
		customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
		return customerDto;
	}

	/**
	 * 
	 * @param customerDto
	 * @return boolean
	 */
	@Override
	public boolean update(CustomerDto customerDto) {
		boolean isUpdated = false;
		AccountsDto accountsDto = customerDto.getAccountsDto();
		if (accountsDto != null) {
			Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber())
					.orElseThrow(() -> new ResourceNotFoundException("Accounts", "Account Number", accountsDto.getAccountNumber()+""));
			
			AccountsMapper.mapToAccounts(accountsDto, accounts);
			accountsRepository.save(accounts);
			
			Customer customer = customerRepository.findById(accounts.getCustomerId())
					.orElseThrow(() -> new ResourceNotFoundException("Customer", "CustomerId", accounts.getCustomerId()+""));
			
			CustomerMapper.mapToCustomer(customerDto, customer);
			customerRepository.save(customer);
			isUpdated = true;
		}
		
		return isUpdated;
	}

	@Override
	public boolean delete(String mobile) {
		Customer customer = customerRepository.findByMobile(mobile)
				.orElseThrow(() -> new ResourceNotFoundException("Customer", "mobile", mobile));
		
		accountsRepository.deleteByCustomerId(customer.getCustomerId());
		customerRepository.deleteById(customer.getCustomerId());
		return true;
	}
}
