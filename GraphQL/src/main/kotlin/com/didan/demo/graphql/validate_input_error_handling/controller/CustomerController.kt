package com.didan.demo.graphql.validate_input_error_handling.controller

import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerDto
import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerNotFound
import com.didan.demo.graphql.validate_input_error_handling.dto.DeleteResponseDto
import com.didan.demo.graphql.validate_input_error_handling.exception.ApplicationErrors
import com.didan.demo.graphql.validate_input_error_handling.service.CustomerService
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CustomerController(private val customerService: CustomerService) {

    val log: Logger = LoggerFactory.getLogger(CustomerController::class.java)

    @QueryMapping
    fun customers(environtment: DataFetchingEnvironment): Flux<CustomerDto> {
        // Lấy thông tin caller-id từ context (Đã được interceptor thêm vào)
        val callerId = environtment.graphQlContext["caller-id"] as String
        log.info("CALLER ID: $callerId")
        return customerService.allCustomers()
    }

    @QueryMapping
    fun customerById(@Argument id: Int): Mono<Any> {
        return customerService.customerById(id)
            .cast(Any::class.java)
            .switchIfEmpty(Mono.just<Any>(CustomerNotFound(id)))
    }

    @MutationMapping
    fun createCustomer(@Argument("customer") @Valid dto: CustomerDto): Mono<CustomerDto> {
        return Mono.just(dto)
            .filter { it.age!! >= 18 }
            .flatMap { customerService.createCustomer(dto) }
            .switchIfEmpty(ApplicationErrors.mustBe18(dto))

    }

    @MutationMapping
    fun updateCustomer(@Argument id: Int, @Argument("customer") @Valid dto: CustomerDto): Mono<CustomerDto> {
        return customerService.updateCustomer(id, dto)
    }

    @MutationMapping
    fun deleteCustomer(@Argument id: Int): Mono<DeleteResponseDto> {
        return customerService.deleteCustomer(id)
    }

}
