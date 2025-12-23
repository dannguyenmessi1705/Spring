package com.didan.demo.graphql.union.controller

import com.didan.demo.graphql.union.dto.Book
import com.didan.demo.graphql.union.dto.Electronics
import com.didan.demo.graphql.union.dto.FruitDto
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@Controller
class SearchController {

    val list: List<Any> = listOf(
        FruitDto("banana", LocalDate.now().plusDays(3)),
        FruitDto("apple", LocalDate.now().plusDays(5)),
        Electronics(UUID.randomUUID(), "mac book", 600, "APPLE"),
        Electronics(UUID.randomUUID(), "phone", 400, "SAMSUNG"),
        Book("java", "venkat")
    )

    @QueryMapping
    fun search(): Flux<Any> {
        return Mono.fromSupplier { ArrayList(list) } // Tạo một bản sao của danh sách ban đầu
            .doOnNext { Collections.shuffle(it) } // Xáo trộn danh sách để đảm bảo tính ngẫu nhiên
            .flatMapIterable { it } // Chuyển đổi danh sách thành Flux
            .take(ThreadLocalRandom.current().nextInt(0, list.size).toLong()) // Lấy một số lượng ngẫu nhiên các phần tử từ danh sách
    }
}

/**
 * Query:
 * {
 *   search {
 *     type:__typename
 *     ... on Fruit {
 *       description
 *       expiryDate
 *     }
 *     ... on Electronics {
 *       id
 *       price
 *       brand
 *       description
 *     }
 *     ... on Book {
 *       title
 *       author
 *     }
 *   }
 * }
 */