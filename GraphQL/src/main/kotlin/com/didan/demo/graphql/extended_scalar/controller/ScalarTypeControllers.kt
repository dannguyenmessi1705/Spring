package com.didan.demo.graphql.extended_scalar.controller

import com.didan.demo.graphql.extended_scalar.dto.AllTypes
import com.didan.demo.graphql.extended_scalar.dto.Car
import com.didan.demo.graphql.extended_scalar.dto.Product
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID

@Controller
class ScalarTypeControllers {

    private val allTypes: AllTypes = AllTypes(
        UUID.randomUUID(),
        10,
        10.12f,
        "atlanta",
        false,
        12000000000L,
        "12".toByte(),
        "100".toShort(),
        BigDecimal.valueOf(1234567890.12345),
        BigInteger.valueOf(1234567889),
        LocalDate.now(),
        LocalTime.now(),
        OffsetDateTime.now(),
        Car.HONDA
    )

    @QueryMapping
    fun get(): Mono<AllTypes> {
        return Mono.just(allTypes)
    }

    @QueryMapping
    fun products(): Flux<Product> {
        return Flux.just(
            Product(
                "banana",
                mapOf(
                    "expiry date" to "2024-12-12",
                    "color" to "yellow"
                )
            ),
            Product(
                "mac",
                mapOf(
                    "cpu" to "8",
                    "RAM" to "32g"
                )
            )
        )
    }
}