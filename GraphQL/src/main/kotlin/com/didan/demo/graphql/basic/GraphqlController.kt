package com.didan.demo.graphql.basic

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
import reactor.core.publisher.Mono;

@Controller
class GraphqlController {

    @QueryMapping
    fun sayHello(): Mono<String> {
        return Mono.just("Hello world!")
    }

    @QueryMapping("sayHelloTo")
    fun sayHelloTo(@Argument name: String): Mono<String> {
        return Mono.fromSupplier { "Hello, $name!" }
    }
}