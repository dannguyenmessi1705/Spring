package com.didan.demo.graphql.subscription.service

import com.didan.demo.graphql.subscription.dto.Action
import com.didan.demo.graphql.subscription.dto.CustomerDto
import com.didan.demo.graphql.subscription.dto.CustomerEvent
import com.didan.demo.graphql.subscription.dto.DeleteResponseDto
import com.didan.demo.graphql.subscription.dto.Status
import com.didan.demo.graphql.subscription.repository.CustomerRepository
import com.didan.demo.graphql.subscription.util.EntityDtoUtil
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CustomerService(private val customerRepository: CustomerRepository, private val customerEventService: CustomerEventService) {

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
            .doOnNext { customerEventService.emitEvent(CustomerEvent(it.id, Action.CREATED)) }
    }

    fun updateCustomer(id: Int, dto: CustomerDto): Mono<CustomerDto> {
        return customerRepository.findById(id)
            .map { EntityDtoUtil.toEntity(id, dto) }
            .flatMap { customerRepository.save(it) }
            .map { EntityDtoUtil.toDto(it) }
            .doOnNext { customerEventService.emitEvent(CustomerEvent(it.id, Action.UPDATED)) }
    }

    fun deleteCustomer(id: Int): Mono<DeleteResponseDto> {
        return customerRepository.deleteById(id)
            .doOnSuccess { customerEventService.emitEvent(CustomerEvent(id, Action.DELETED)) }
            .thenReturn(DeleteResponseDto(id, Status.SUCCESS))
            .onErrorReturn(DeleteResponseDto(id, Status.FAILURE))
    }
}