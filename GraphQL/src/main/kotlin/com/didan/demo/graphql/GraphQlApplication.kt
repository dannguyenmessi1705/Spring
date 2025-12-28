package com.didan.demo.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication(scanBasePackages = ["com.didan.demo.graphql.validate_input_error_handling"])
@EnableR2dbcRepositories(basePackages = ["com.didan.demo.graphql.validate_input_error_handling"])
class GraphQlApplication

fun main(args: Array<String>) {
    runApplication<GraphQlApplication>(*args)
}
