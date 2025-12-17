package com.didan.demo.graphql.custom_type.service

import com.didan.demo.graphql.custom_type.dto.Customer
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Slf4j
class CustomerService {

    private val flux: Flux<Customer> = Flux.just(
        Customer(1, "sam", 20, "atlanta"),
        Customer(2, "jake", 10, "las vegas"),
        Customer(3, "alan", 15, "miami"),
        Customer(4, "mike", 5, "houston"),
    )

    fun allCustomers(): Flux<Customer> {
        return flux;
    }

    fun customerById(id: Int): Mono<Customer> {
        return flux.filter { it.id == id } // Lọc theo id
            .next() // Lấy phần tử đầu tiên khớp với điều kiện
    }

    fun nameContains(name: String): Flux<Customer> {
        return flux.filter { it.name.contains(name) } // Lọc theo tên chứa chuỗi name
    }
}