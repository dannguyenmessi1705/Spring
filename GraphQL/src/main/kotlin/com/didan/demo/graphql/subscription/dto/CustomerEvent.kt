package com.didan.demo.graphql.subscription.dto

data class CustomerEvent(
    var id: Int? = null,
    var action: Action? = null
)
