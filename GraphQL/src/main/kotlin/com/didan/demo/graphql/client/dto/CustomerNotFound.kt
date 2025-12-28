package com.didan.demo.graphql.client.dto

data class CustomerNotFound(
    var id: Int? = null,
    val message: String = "User not found"
)
