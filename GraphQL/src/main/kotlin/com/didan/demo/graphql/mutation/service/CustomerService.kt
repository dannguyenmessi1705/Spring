package com.didan.demo.graphql.mutation.service

import com.didan.demo.graphql.mutation.dto.CustomerDto
import com.didan.demo.graphql.mutation.dto.DeleteResponseDto
import com.didan.demo.graphql.mutation.dto.Status
import com.didan.demo.graphql.mutation.repository.CustomerRepository
import com.didan.demo.graphql.mutation.util.EntityDtoUtil
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun allCustomers(): Flux<CustomerDto> {
        return customerRepository.findAll()
            .map { EntityDtoUtil.toDto(it) }
    }

    fun customerById(id: Int): Mono<CustomerDto> {
        return customerRepository.findById(id)
            .map { EntityDtoUtil.toDto(it) }
    }

    fun createCustomer(dto: CustomerDto): Mono<CustomerDto> {
        return Mono.just(dto)
            .map { EntityDtoUtil.toEntity(it) }
            .flatMap { customerRepository.save(it) }
            .map { EntityDtoUtil.toDto(it) }
    }

    fun updateCustomer(id: Int, dto: CustomerDto): Mono<CustomerDto> {
        return customerRepository.findById(id)
            .map { EntityDtoUtil.toEntity(id, dto) }
            .flatMap { customerRepository.save(it) }
            .map { EntityDtoUtil.toDto(it) }
    }

    fun deleteCustomer(id: Int): Mono<DeleteResponseDto> {
        return customerRepository.deleteById(id)
            .thenReturn(DeleteResponseDto(id, Status.SUCCESS))
            .onErrorReturn(DeleteResponseDto(id, Status.FAILURE))
    }
}