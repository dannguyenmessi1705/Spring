package com.didan.microservices.accounts.service;

import com.didan.microservices.accounts.dto.CustomerDto;

public interface IAccountsService {
	/**
	 * 
	 * @param customerDto - customerDto Object
	 */
	void createAccounts(CustomerDto customerDto);
	
	/**
	 * @param mobile - số điện thoại khách hàng
	 * @return - trả về CustomerDto
	 */
	CustomerDto fetch(String mobile);
	
	/**
	 * 
	 * @param customerDto
	 * @return boolean
	 */
	boolean update(CustomerDto customerDto);
	
	/**
	 * 
	 * @param mobile
	 * @return boolean
	 */
	boolean delete(String mobile);
}
