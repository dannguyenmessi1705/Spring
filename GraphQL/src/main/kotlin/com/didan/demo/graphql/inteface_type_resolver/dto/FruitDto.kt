package com.didan.demo.graphql.inteface_type_resolver.dto

import java.time.LocalDate
import java.util.UUID

data class FruitDto(
    val id: UUID,
    val description: String,
    val price: Int,
    val expiryDate: LocalDate
)
