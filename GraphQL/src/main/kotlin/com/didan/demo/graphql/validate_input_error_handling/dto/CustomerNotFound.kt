package com.didan.demo.graphql.validate_input_error_handling.dto

data class CustomerNotFound(
    var id: Int? = null,
    val message: String = "User not found"
) {
}