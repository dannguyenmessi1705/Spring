package com.didan.demo.graphql.cache

import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.graphql.autoconfigure.GraphQlSourceBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Configuration
class OperationCacheConfig {

    val log: Logger = LoggerFactory.getLogger(OperationCacheConfig::class.java)

    /**
     * request body
     * exe document
     * parse and validate function
     * exe document
     *
     * Gợi ý: Nên sử dụng variables với các operation name để tối ưu hóa việc cache các truy vấn GraphQL.
     * Nên sử dụng CaffeinCache hoặc RedisCache để lưu trữ các truy vấn đã được phân tích trước thay vì sử dụng ConcurrentHashMap.
     */

    /**
     * Cấu hình GraphQlSourceBuilderCustomizer để sử dụng PreparsedDocumentProvider tùy chỉnh
     * nhằm tối ưu hóa hiệu suất xử lý các truy vấn GraphQL đã được phân tích trước.
     * @param customProvider PreparsedDocumentProvider tùy chỉnh được định nghĩa trong bean customProvider()
     */
    @Bean
    fun sourceBuilderCustomizer(customProvider: PreparsedDocumentProvider): GraphQlSourceBuilderCustomizer {
        return GraphQlSourceBuilderCustomizer { c ->
            c.configureGraphQl { builder -> builder.preparsedDocumentProvider(customProvider) }
        }
    }


    /**
     * Định nghĩa PreparsedDocumentProvider tùy chỉnh để lưu trữ và truy xuất các truy vấn GraphQL đã được phân tích trước
     * từ một bộ nhớ đệm ConcurrentHashMap.
     * Mục đích là để cải thiện hiệu suất bằng cách tránh việc phân tích lại các truy vấn đã được xử lý trước đó.
     */
    @Bean
    fun customProvider(): PreparsedDocumentProvider {
        val map = ConcurrentHashMap<String, PreparsedDocumentEntry>()
        return PreparsedDocumentProvider { executionInput, parseAndValidateFunction ->
            val documentEntry: PreparsedDocumentEntry = map.computeIfAbsent(executionInput.query, {
                log.info("Not found : $it")
                val r: PreparsedDocumentEntry = parseAndValidateFunction.apply(executionInput)
                log.info("Caching : $it")
                r
            })
            CompletableFuture.completedFuture(documentEntry)
        }
    }
}