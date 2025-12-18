package com.didan.demo.graphql.data_fetching_multiple.controller

import com.didan.demo.graphql.data_fetching_multiple.dto.Customer
import com.didan.demo.graphql.data_fetching_multiple.service.CustomerService
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class CustomerController(private val customerService: CustomerService, private val environment: Environment) {

    private val log: Logger = LoggerFactory.getLogger(CustomerController::class.java)

    // Biến environment có thể được sử dụng để truy cập các biến môi trường hoặc cấu hình ứng dụng nếu cần thiết, mặc định không cần thiết phải truyền vào nếu không sử dụng
    @QueryMapping
    fun customers(environment: DataFetchingEnvironment): Flux<Customer> {
        log.info("Customer : ${environment.document}")
        log.info("$environment.operationDefinition")
        return this.customerService.allCustomers()
    }
}