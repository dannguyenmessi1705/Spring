package com.didan.demo.graphql.wiring_beans.config

import com.didan.demo.graphql.wiring_beans.service.CustomerOrderDataFetcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class DataFetchingWiringConfig(private val dataFetcher: CustomerOrderDataFetcher) {

    @Bean
    fun configurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { c ->
            c.type("Query") { b -> b.dataFetcher("customers", dataFetcher) }
        }
    }
}