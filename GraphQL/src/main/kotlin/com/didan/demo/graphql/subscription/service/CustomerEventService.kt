package com.didan.demo.graphql.subscription.service

import com.didan.demo.graphql.subscription.dto.CustomerEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Service
class CustomerEventService {

    // Biến sink để phát sự kiện đến các subscriber
    // multicast(): Cho phép nhiều subscriber nhận cùng một sự kiện
    // onBackpressureBuffer(): Xử lý trường hợp subscriber không kịp nhận sự kiện bằng cách lưu vào bộ đệm
    private val sink: Sinks.Many<CustomerEvent> = Sinks.many().multicast().onBackpressureBuffer()
    private val flux: Flux<CustomerEvent> = sink.asFlux().cache(0) // Lưu trữ các sự kiện mới nhất để phát lại cho các subscriber mới

    /**
     * Phương thức phát sự kiện mới đến các subscriber
     */
    fun emitEvent(event: CustomerEvent): Unit {
        sink.tryEmitNext(event) // Phát sự kiện mới đến các subscriber
    }


    /**
     * Phương thức để các subscriber đăng ký nhận sự kiện
     */
    fun subscribe(): Flux<CustomerEvent> {
        return flux
    }


}