package com.didan.demo.graphql.validate_input_error_handling.exception

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExceptionResolver : DataFetcherExceptionResolver {

    /**
     * Xử lý ngoại lệ và chuyển đổi chúng thành GraphQLError.
     */
    override fun resolveException(exception: Throwable?, environment: DataFetchingEnvironment?): Mono<List<GraphQLError?>?>? {
        val ex = toApplicationException(exception)
        return Mono.just(
            listOf(
                GraphqlErrorBuilder.newError()
                    .message(ex.message)
                    .errorType(ex.errorType)
                    .extensions(ex.extensions)
                    .build()
            )
        )
    }

    /**
     * Chuyển đổi Throwable thành ApplicationException.
     */
    private fun toApplicationException(throwable: Throwable?): ApplicationException {
        return when (throwable) {
            is ApplicationException -> throwable
            is ConstraintViolationException -> {
                val violations = throwable.constraintViolations.map {
                    mapOf(
                        "path" to it.propertyPath.toString(),
                        "message" to it.message,
                        "invalidValue" to (it.invalidValue?.toString() ?: "null")
                    )
                }
                ApplicationException(
                    ErrorType.BAD_REQUEST,
                    "Validation failed",
                    mutableMapOf("violations" to violations)
                )
            }
            else -> ApplicationException(ErrorType.INTERNAL_ERROR, throwable?.message, mutableMapOf<String, Any>())
        }
    }
}
