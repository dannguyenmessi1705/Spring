package com.didan.demo.graphql.subscription.repository

import com.didan.demo.graphql.subscription.entity.Customer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveCrudRepository<Customer, Int> {
}