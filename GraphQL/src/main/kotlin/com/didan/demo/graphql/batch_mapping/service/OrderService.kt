package com.didan.demo.graphql.batch_mapping.service

import com.didan.demo.graphql.batch_mapping.dto.CustomerOrderDto
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.Duration

@Service
@Slf4j
class OrderService {

    private val map: Map<String, List<CustomerOrderDto>> = mapOf(
        "sam" to listOf(
            CustomerOrderDto(UUID.randomUUID(), "sam-product-1"),
            CustomerOrderDto(UUID.randomUUID(), "sam-product-2"),
        ),
        "mike" to listOf(
            CustomerOrderDto(UUID.randomUUID(), "mike-product-1"),
            CustomerOrderDto(UUID.randomUUID(), "mike-product-2"),
            CustomerOrderDto(UUID.randomUUID(), "mike-product-3")
        )
    )

    fun ordersByCustomerName(names: List<String>): Flux<List<CustomerOrderDto>> {
        return Flux.fromIterable(names) // Tạo Flux từ danh sách tên
            .flatMapSequential {  // Sử dụng flatMapSequential để duy trì thứ tự (Nếu không sẽ bị lộn xộn do độ trễ ngẫu nhiên)
                fetchOrders(it) // Lấy đơn hàng cho từng tên
                    .defaultIfEmpty(Collections.emptyList()) // Trả về danh sách rỗng nếu không có đơn hàng (Nếu không có sẽ trả về lỗi do Mono rỗng)
            }
    }

    private fun fetchOrders(name: String): Mono<List<CustomerOrderDto>> {
        return Mono.justOrEmpty(map[name]) // Lấy danh sách đơn hàng từ map
            .delayElement(java.time.Duration.ofMillis(ThreadLocalRandom.current().nextInt(0, 500).toLong())) // Mô phỏng độ trễ ngẫu nhiên
    }
}