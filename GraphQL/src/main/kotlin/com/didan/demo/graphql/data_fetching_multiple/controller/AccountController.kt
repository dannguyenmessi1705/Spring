package com.didan.demo.graphql.data_fetching_multiple.controller

import com.didan.demo.graphql.data_fetching_multiple.dto.Customer
import com.didan.demo.graphql.data_fetching_multiple.dto.Account
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@Controller
class AccountController {

    @SchemaMapping(
        typeName = "Customer", // Tên của kiểu GraphQL mà bạn muốn ánh xạ
        field = "account" // Tên của trường trong kiểu GraphQL mà bạn muốn ánh xạ
    )
    fun account(customer: Customer): Mono<Account>  {
        val type = if (ThreadLocalRandom.current().nextBoolean()) "CHECKING" else "SAVING"
        return Mono.just(
            Account(UUID.randomUUID(), ThreadLocalRandom.current().nextInt(1, 1000), type)
        )
    }
}