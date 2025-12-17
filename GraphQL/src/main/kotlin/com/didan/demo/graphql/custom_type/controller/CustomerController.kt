package com.didan.demo.graphql.custom_type.controller

import com.didan.demo.graphql.custom_type.dto.Customer
import com.didan.demo.graphql.custom_type.service.CustomerService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CustomerController(private val customerService: CustomerService) {

    @QueryMapping
    fun customers(): Flux<Customer> {
        return this.customerService.allCustomers();
    }

    @QueryMapping
    fun customerById(@Argument id: Int): Mono<Customer> {
        return this.customerService.customerById(id);
    }

    @QueryMapping
    fun customersNameContains(@Argument name: String): Flux<Customer> {
        return this.customerService.nameContains(name);
    }
}