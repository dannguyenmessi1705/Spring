package com.didan.demo.graphql.inteface_type_resolver.controller

import com.didan.demo.graphql.inteface_type_resolver.dto.Book
import com.didan.demo.graphql.inteface_type_resolver.dto.Electronics
import com.didan.demo.graphql.inteface_type_resolver.dto.FruitDto
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.util.UUID

@Controller
class ProductController {

    @QueryMapping
    fun products(): Flux<Any> {
        return Flux.just(
            FruitDto(UUID.randomUUID(), "banana", 1, LocalDate.now().plusDays(3)),
            FruitDto(UUID.randomUUID(), "apple", 2, LocalDate.now().plusDays(5)),
            Electronics(UUID.randomUUID(), "mac book", 600, "APPLE"),
            Electronics(UUID.randomUUID(), "phone", 400, "SAMSUNG"),
            Book(UUID.randomUUID(), "java", 40, "venkat")
        )
    }
}

/**
 * Query:
 * {
 *   products {
 *     id
 *     description
 *     price
 *     type: __typename
 *     ... on Book {
 *       author
 *     }
 *     ... on Electronics {
 *       brand
 *     }
 *     ... on Fruit {
 *       expiryDate
 *     }
 *   }
 * }
 */