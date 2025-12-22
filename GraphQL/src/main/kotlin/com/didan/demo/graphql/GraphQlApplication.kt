package com.didan.demo.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.didan.demo.graphql.extended_scalar"])
class GraphQlApplication

fun main(args: Array<String>) {
    runApplication<GraphQlApplication>(*args)
}
