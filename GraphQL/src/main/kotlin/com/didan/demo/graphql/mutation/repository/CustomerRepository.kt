package com.didan.demo.graphql.mutation.repository

import com.didan.demo.graphql.mutation.entity.Customer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveCrudRepository<Customer, Int> {
}