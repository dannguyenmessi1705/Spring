package com.didan.demo.graphql.subscription.controller

import com.didan.demo.graphql.subscription.dto.CustomerEvent
import com.didan.demo.graphql.subscription.service.CustomerEventService
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class CustomerEventController(private val customerEventService: CustomerEventService) {

    @SubscriptionMapping
    fun customerEvents(): Flux<CustomerEvent> {
        return customerEventService.subscribe(); // Trả về luồng sự kiện để các subscriber đăng ký nhận các sự kiện
    }
}