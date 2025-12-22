package com.didan.demo.graphql.wiring_beans.service

import com.didan.demo.graphql.wiring_beans.dto.Customer
import com.didan.demo.graphql.wiring_beans.dto.CustomerOrderDto
import com.didan.demo.graphql.wiring_beans.dto.CustomerWithOrder
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.function.UnaryOperator

@Service
class CustomerOrderDataFetcher : DataFetcher<Flux<CustomerWithOrder>> {

    private val log: Logger = LoggerFactory.getLogger(CustomerOrderDataFetcher::class.java)

    @Autowired
    private val customerService: CustomerService? = null

    @Autowired
    private val orderService: OrderService? = null

    @Throws(Exception::class)
    override fun get(environment: DataFetchingEnvironment): Flux<CustomerWithOrder> {
        val includeOrders = environment.getSelectionSet().contains("orders")
        log.info(includeOrders.toString())
        return this.customerService!!.allCustomers()
            .map<CustomerWithOrder>(Function { c: Customer -> CustomerWithOrder(c.id, c.name, c.age, c.city, mutableListOf<CustomerOrderDto>()) })
            .transform<CustomerWithOrder>(this.updateOrdersTransformer(includeOrders))
    }

    private fun updateOrdersTransformer(includeOrders: Boolean): UnaryOperator<Flux<CustomerWithOrder>> {
        return if (includeOrders) UnaryOperator { f: Flux<CustomerWithOrder> -> f.flatMapSequential<CustomerWithOrder>(Function { customerWithOrder: CustomerWithOrder -> this.fetchOrders(customerWithOrder) }) } else UnaryOperator { f: Flux<CustomerWithOrder> -> f }
    }

    private fun fetchOrders(customerWithOrder: CustomerWithOrder): Mono<CustomerWithOrder> {
        return this.orderService!!.ordersByCustomerName(customerWithOrder.name)
            .collectList()
            .doOnNext { customerWithOrder.orders = it }
            .thenReturn<CustomerWithOrder>(customerWithOrder)
    }
}
