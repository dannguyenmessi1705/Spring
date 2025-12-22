package com.didan.demo.graphql.wiring_beans.service

import com.didan.demo.graphql.wiring_beans.dto.Customer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime

@Service
class CustomerService {

    private val log: Logger = LoggerFactory.getLogger(CustomerService::class.java)

    val flux: Flux<Customer> = Flux.just(
        Customer(1, "sam", 20, "atlanta"),
        Customer(2, "jake", 10, "las vegas"),
        Customer(3, "mike", 15, "miami"),
        Customer(4, "john", 5, "houston")
    )

    fun allCustomers(): Flux<Customer> {
        return flux.delayElements(Duration.ofSeconds(1))
            .doOnNext { log.info("${LocalDateTime.now()} : Customer ${it.name}") }
    }
}