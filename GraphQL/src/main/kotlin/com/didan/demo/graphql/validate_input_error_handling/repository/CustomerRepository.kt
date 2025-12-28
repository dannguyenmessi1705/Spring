package com.didan.demo.graphql.validate_input_error_handling.repository

import com.didan.demo.graphql.validate_input_error_handling.entity.Customer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveCrudRepository<Customer, Int> {
}