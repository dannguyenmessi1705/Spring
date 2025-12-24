package com.didan.demo.graphql.mutation.controller

import com.didan.demo.graphql.mutation.dto.CustomerDto
import com.didan.demo.graphql.mutation.dto.DeleteResponseDto
import com.didan.demo.graphql.mutation.service.CustomerService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CusotmerController(private val customerService: CustomerService) {

    @QueryMapping
    fun customers(): Flux<CustomerDto> {
        return customerService.allCustomers()
    }

    @QueryMapping
    fun customerById(@Argument id: Int): Mono<CustomerDto> {
        return customerService.customerById(id)
    }

    @MutationMapping
    fun createCustomer(@Argument("customer") dto: CustomerDto): Mono<CustomerDto> {
        return customerService.createCustomer(dto)
    }

    @MutationMapping
    fun updateCustomer(@Argument id: Int, @Argument("customer") dto: CustomerDto): Mono<CustomerDto> {
        return customerService.updateCustomer(id, dto)
    }

    @MutationMapping
    fun deleteCustomer(@Argument id: Int): Mono<DeleteResponseDto> {
        return customerService.deleteCustomer(id)
    }

}