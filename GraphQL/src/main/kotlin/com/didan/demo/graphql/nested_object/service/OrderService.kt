package com.didan.demo.graphql.nested_object.service

import com.didan.demo.graphql.nested_object.dto.CustomerOrderDto
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.Collections
import java.util.UUID

@Service
@Slf4j
class OrderService {

    private val map: Map<String, List<CustomerOrderDto>> = mapOf(
        "sam" to listOf(
            CustomerOrderDto(UUID.randomUUID(), "sam-product-1"),
            CustomerOrderDto(UUID.randomUUID(), "sam-product-2"),
        ),
        "mike" to listOf(
            CustomerOrderDto(UUID.randomUUID(), "mike-product-1"),
            CustomerOrderDto(UUID.randomUUID(), "mike-product-2"),
            CustomerOrderDto(UUID.randomUUID(), "mike-product-3")
        )
    )

    fun ordersByCustomerName(name: String): Flux<CustomerOrderDto> {
        return Flux.fromIterable(map.getOrDefault(name, Collections.emptyList()))
    }
}