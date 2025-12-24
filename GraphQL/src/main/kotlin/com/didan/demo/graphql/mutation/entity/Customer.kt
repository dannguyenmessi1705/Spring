package com.didan.demo.graphql.mutation.entity

import org.springframework.data.annotation.Id

data class Customer(
    @Id
    var id: Int? = null,
    var name: String? = null,
    var age: Int? = null,
    var city: String? = null
)