package com.didan.demo.graphql.validate_input_error_handling.exception

import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerDto
import org.springframework.graphql.execution.ErrorType
import reactor.core.publisher.Mono
import java.util.Map

object ApplicationErrors {
    fun <T> noSuchUser(id: Int): Mono<T?> {
        return Mono.error<T?>(
            ApplicationException(
                ErrorType.BAD_REQUEST, "No such user", Map.of<String, Any>(
                    "customerId", id
                )
            )
        )
    }

    fun <T> mustBe18(dto: CustomerDto): Mono<T?> {
        return Mono.error<T?>(
            ApplicationException(
                ErrorType.BAD_REQUEST, "Must be 18 or above", Map.of<String, Any>(
                    "input", dto
                )
            )
        )
    }
}
