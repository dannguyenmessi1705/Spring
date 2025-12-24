package com.didan.demo.graphql.mutation.dto

data class CustomerDto(
    var id: Int? = null,
    var name: String? = null,
    var age: Int? = null,
    var city: String? = null
)