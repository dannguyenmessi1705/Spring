package com.didan.demo.graphql.validate_input_error_handling.filter

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
class HeaderCheckFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange?, chain: WebFilterChain?): Mono<Void?>? {
        // Kiểm tra header "caller-id" có tồn tại hay không
        val isEmpty = exchange?.request?.headers?.getOrEmpty("caller-id")?.isEmpty()

        // Nếu header tồn tại, tiếp tục chuỗi lọc; nếu không, trả về lỗi BAD_REQUEST
        return if (!isEmpty!!) chain?.filter(exchange) else Mono.fromRunnable { exchange?.response?.statusCode = HttpStatus.BAD_REQUEST }
    }
}