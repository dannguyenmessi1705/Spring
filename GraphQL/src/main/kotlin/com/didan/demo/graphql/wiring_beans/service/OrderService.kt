package com.didan.demo.graphql.wiring_beans.service

import com.didan.demo.graphql.wiring_beans.dto.CustomerOrderDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime
import java.util.Collections
import java.util.UUID

@Service
class OrderService {

    private val log: Logger = LoggerFactory.getLogger(OrderService::class.java)

    private val map: Map<String, List<CustomerOrderDto>> = mapOf(
        "sam" to listOf(
            CustomerOrderDto(UUID.randomUUID(), "sam-product-1"),
            CustomerOrderDto(UUID.randomUUID(), "sam-product-2")
        )
    )

    fun ordersByCustomerName(name: String): Flux<CustomerOrderDto> {
        return Flux.fromIterable(map.getOrDefault(name, Collections.emptyList()))
            .delayElements(Duration.ofSeconds(1))
            .doOnNext { log.info("${LocalDateTime.now()} : Orders for $name") }
    }
}