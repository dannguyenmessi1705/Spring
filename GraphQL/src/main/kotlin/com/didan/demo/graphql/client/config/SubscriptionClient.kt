package com.didan.demo.graphql.client.config

import com.didan.demo.graphql.client.dto.CustomerEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.client.WebSocketGraphQlClient
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux

@Service
class SubscriptionClient(@Value($$"${customer.events.subscription.url}") baseUrl: String) {
    private val client: WebSocketGraphQlClient

    init {
        this.client = WebSocketGraphQlClient.builder(baseUrl, ReactorNettyWebSocketClient()).build()
    }

    /*
        subscription{
            customerEvents{
                id
                action
            }
        }
     */
    fun customerEvents(): Flux<CustomerEvent?> {
        val doc = "        subscription{\n" +
                "            customerEvents{\n" +
                "                id\n" +
                "                action\n" +
                "            }\n" +
                "        }"
        return this.client.document(doc)
            .retrieveSubscription("customerEvents")
            .toEntity<CustomerEvent?>(CustomerEvent::class.java)
    }
}
