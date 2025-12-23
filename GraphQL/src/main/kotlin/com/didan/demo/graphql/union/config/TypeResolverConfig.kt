package com.didan.demo.graphql.union.config

import com.didan.demo.graphql.union.dto.FruitDto
import graphql.schema.TypeResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.ClassNameTypeResolver
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class TypeResolverConfig {

    /**
     * Cấu hình RuntimeWiringConfigurer để đăng ký TypeResolver tùy chỉnh cho kiểu GraphQL "Result"
     * @param customTypeResolver TypeResolver tùy chỉnh được định nghĩa trong bean customTypeResolver()
     */
    @Bean
    fun configurer(customTypeResolver: TypeResolver): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { c ->
            c.type("Result") { b ->
                b.typeResolver(customTypeResolver)
            }
        }
    }

    /**
     * Định nghĩa TypeResolver tùy chỉnh để ánh xạ các lớp Kotlin với các kiểu GraphQL tương ứng
     * @return TypeResolver tùy chỉnh
     */
    @Bean
    fun customTypeResolver(): TypeResolver {
        val resolver: ClassNameTypeResolver = object : ClassNameTypeResolver() {}
        resolver.addMapping(FruitDto::class.java, "Fruit") // Ánh xạ FruitDto với kiểu GraphQL "Fruit"
        return resolver
    }
}