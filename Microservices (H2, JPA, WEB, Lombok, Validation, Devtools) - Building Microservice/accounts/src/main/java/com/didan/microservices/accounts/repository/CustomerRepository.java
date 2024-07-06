package com.didan.microservices.accounts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.didan.microservices.accounts.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	Optional<Customer> findByMobile(String mobile);
}
