package com.didan.demo.graphql.nested_object.controller

import com.didan.demo.graphql.nested_object.dto.Customer
import com.didan.demo.graphql.nested_object.dto.CustomerOrderDto
import com.didan.demo.graphql.nested_object.service.CustomerService
import com.didan.demo.graphql.nested_object.service.OrderService
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

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

    // Hàm này sẽ được gọi khi truy vấn đến trường "orders" của đối tượng Customer (khi gọi query customers)
    @SchemaMapping(
        typeName = "Customer",
        field = "orders"
    )
    fun orders(
        customer: Customer, // Đối tượng Customer được lấy từ ngữ cảnh
        @Argument limit: Long // Tham số limit được truyền từ truy vấn GraphQL
    ): Flux<CustomerOrderDto> {
        log.info("Orders method invoked for {}", customer.name)
        return this.orderService.ordersByCustomerName(customer.name).take(limit)
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
 *     orders(limit: 0) {
 *       description
 *       id
 *     }
 *   }
 * }
 */