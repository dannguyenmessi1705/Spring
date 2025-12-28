package com.didan.demo.graphql.validate_input_error_handling.interceptor

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestInterceptor : WebGraphQlInterceptor {
    override fun intercept(request: WebGraphQlRequest?, chain: WebGraphQlInterceptor.Chain?): Mono<WebGraphQlResponse?>? {
        // Lấy giá trị caller-id từ header và đưa vào GraphQL Context
        val headers = request?.headers?.getOrEmpty("caller-id")
        // Nếu không có header caller-id, gán giá trị rỗng
        val callerId = if (headers!!.isEmpty()) "" else headers[0]
        // Cấu hình lại request để thêm GraphQL Context với caller-id lấy từ header
        request.configureExecutionInput { _, builder -> builder.graphQLContext(mapOf<String, String>("caller-id" to callerId)).build() }
        // Tiếp tục chuỗi interceptor
        return chain?.next(request)

    }
}