package com.didan.demo.graphql.validate_input_error_handling.config

import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerDto
import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerNotFound
import graphql.schema.TypeResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.ClassNameTypeResolver
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class TypeResolverConfig {

    /**
     * Cấu hình TypeResolver để ánh xạ kiểu Kotlin sang GraphQL union types.
     */
    @Bean
    fun typeResolver(): TypeResolver {
        val resolver: ClassNameTypeResolver = ClassNameTypeResolver()
        resolver.addMapping(CustomerDto::class.java, "Customer")
        resolver.addMapping(CustomerNotFound::class.java, "CustomerNotFound")
        return resolver
    }

    /**
     * Cấu hình RuntimeWiringConfigurer để đăng ký TypeResolver cho GraphQL union "CustomerResponse".
     */
    @Bean
    fun configurer(typeResolver: TypeResolver): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder ->
            wiringBuilder.type("CustomerResponse") { typeWiring ->
                typeWiring.typeResolver(typeResolver)
            }
        }
    }
}
