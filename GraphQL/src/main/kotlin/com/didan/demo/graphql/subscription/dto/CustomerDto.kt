package com.didan.demo.graphql.subscription.dto

data class CustomerDto(
    var id: Int? = null,
    var name: String? = null,
    var age: Int? = null,
    var city: String? = null
)