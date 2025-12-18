package com.didan.demo.graphql.data_fetching_multiple.dto

import java.util.UUID

data class Account(val id: UUID, val amount: Int, val accountType: String) {
}