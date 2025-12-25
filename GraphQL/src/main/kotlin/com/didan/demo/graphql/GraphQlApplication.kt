package com.didan.demo.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication(scanBasePackages = ["com.didan.demo.graphql.subscription"])
@EnableR2dbcRepositories(basePackages = ["com.didan.demo.graphql.subscription"])
class GraphQlApplication

fun main(args: Array<String>) {
    runApplication<GraphQlApplication>(*args)
}
