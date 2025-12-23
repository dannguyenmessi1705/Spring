package com.didan.demo.graphql.inteface_type_resolver.dto

import java.util.UUID

data class Book(val id: UUID, val description: String, val price: Int, val author: String)
