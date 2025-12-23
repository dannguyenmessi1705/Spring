package com.didan.demo.graphql.union.dto

import java.util.UUID

data class Electronics(
    val id: UUID,
    val description: String,
    val price: Int,
    val brand: String
)