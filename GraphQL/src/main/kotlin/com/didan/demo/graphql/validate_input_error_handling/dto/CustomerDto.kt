package com.didan.demo.graphql.validate_input_error_handling.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CustomerDto(
    var id: Int? = null,
    @field:Size(min = 1, message = "name must not be empty")
    var name: String? = null,
    @field:NotNull(message = "age must not be null")
    @field:Min(value = 0, message = "age must be >= 0")
    var age: Int? = null,
    @field:Size(min = 1, message = "city must not be empty")
    var city: String? = null
)
