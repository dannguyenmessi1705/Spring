package com.didan.demo.graphql.data_fetching_multiple.controller

import com.didan.demo.graphql.data_fetching_multiple.dto.Address
import com.didan.demo.graphql.data_fetching_multiple.dto.Customer
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class AddressController {

    @SchemaMapping(
        typeName = "Customer", // Tên của kiểu GraphQL mà bạn muốn ánh xạ
        field = "address" // Tên của trường trong kiểu GraphQL mà bạn muốn ánh xạ
    )
    fun address(customer: Customer): Mono<Address>  {
        return Mono.just(
            Address("${customer.name}'s Street", "${customer.name}'s City")
        )
    }
}