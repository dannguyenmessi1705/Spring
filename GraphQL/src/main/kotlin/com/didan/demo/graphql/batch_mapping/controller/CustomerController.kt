package com.didan.demo.graphql.batch_mapping.controller

import com.didan.demo.graphql.batch_mapping.dto.Customer
import com.didan.demo.graphql.batch_mapping.dto.CustomerOrderDto
import com.didan.demo.graphql.batch_mapping.service.CustomerService
import com.didan.demo.graphql.batch_mapping.service.OrderService
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Controller
@Slf4j
class CustomerController(private val customerService: CustomerService, private val orderService: OrderService) {

    private val log = LoggerFactory.getLogger(CustomerController::class.java)

    // @QueryMapping
    @SchemaMapping(
        typeName = "Query"
    )
    fun customers(): Flux<Customer> {
        return this.customerService.allCustomers();
    }

    // Hàm này sẽ được gọi khi truy vấn đến trường "orders" của đối tượng Customer (khi gọi query customers), BatchMapping sẽ tối ưu hơn SchemaMapping bằng cách gom nhóm các yêu cầu lấy orders của nhiều customers lại với nhau
    // Fix N+1 problem
    @BatchMapping(
        typeName = "Customer",
        field = "orders"
    )
    fun orders(
        list: List<Customer>, // Đối tượng Customer được lấy từ ngữ cảnh
    ): Flux<List<CustomerOrderDto>> {
        log.info("Orders method invoked for {}", list)
        return this.orderService.ordersByCustomerName(
            list.stream().map { it.name }.collect(Collectors.toList())
        )
    }

    // Ví dụ này sẽ ghi đè trường age của Customer để trả về giá trị cố định 100 cho tất cả khách hàng
    @SchemaMapping(
        typeName = "Customer",
        field = "age"
    )
    fun age(): Mono<Int> {
        return Mono.just(100);
    }
}

/**
 * GRAPHQL QUERY EXAMPLE:
 * {
 *   customers {
 *     city
 *     age
 *     id
 *     name
 *     orders {
 *       description
 *       id
 *     }
 *   }
 * }
 */