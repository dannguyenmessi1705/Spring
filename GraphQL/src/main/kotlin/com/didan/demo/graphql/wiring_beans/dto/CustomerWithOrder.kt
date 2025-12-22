package com.didan.demo.graphql.wiring_beans.dto

data class CustomerWithOrder(val id: Int, val name: String, val age: Int, val city: String, var orders: List<CustomerOrderDto>) {
}