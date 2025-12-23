package com.didan.demo.graphql.union.dto

import java.time.LocalDate

data class FruitDto(
    val description: String,
    val expiryDate: LocalDate
)
