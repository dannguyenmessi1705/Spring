package com.didan.demo.graphql.data_fetching_multiple.service

import com.didan.demo.graphql.data_fetching_multiple.dto.Customer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CustomerService {

    private val flux: Flux<Customer> = Flux.just(
        Customer(1, "sam", 20, "atlanta"),
        Customer(2, "jake", 10, "las vegas"),
        Customer(3, "mike", 15, "miami"),
        Customer(4, "john", 5, "houston"),
    )

    fun allCustomers(): Flux<Customer> { return flux }
}