package com.didan.demo.graphql.client.service

import com.didan.demo.graphql.client.config.CustomerClient
import com.didan.demo.graphql.client.config.SubscriptionClient
import com.didan.demo.graphql.client.dto.CustomerDto
import com.didan.demo.graphql.client.dto.CustomerEvent
import com.didan.demo.graphql.client.dto.DeleteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.graphql.client.ClientGraphQlResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.function.Consumer
import java.util.function.Function

@Service
class ClientDemo : CommandLineRunner {
    @Autowired
    private val client: CustomerClient? = null

    @Autowired
    private val subscriptionClient: SubscriptionClient? = null

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        this.subscriptionClient!!
            .customerEvents()
            .doOnNext(Consumer { e: CustomerEvent? -> println(" ** " + e.toString() + " ** ") })
            .subscribe()
        allCustomersDemo()
            .then<Void?>(this.customerByIdDemo())
            .then<Void?>(this.createCustomerDemo())
            .then<Void?>(this.updateCustomerDemo())
            .then<Void?>(this.deleteCustomerDemo())
            .subscribe()
    }

    private fun rawQueryDemo(): Mono<Void?> {
        val query = "                {\n" +
                "                   a: customers{\n" +
                "                        id\n" +
                "                        name\n" +
                "                        age\n" +
                "                        city\n" +
                "                    }\n" +
                "                }"

        val mono = this.client!!.rawQuery(query)
            .map<MutableList<CustomerDto?>?>(Function { cr: ClientGraphQlResponse? -> cr!!.field("a").toEntityList<CustomerDto?>(CustomerDto::class.java) })

        return this.executor<MutableList<CustomerDto?>?>("Raw Query", mono)
    }

    private val customerById: Mono<Void?>
        get() = this.executor<Any?>("getCustomerById", this.client!!.getCustomerByIdWithUnion(5))

    private fun allCustomersDemo(): Mono<Void?> {
        return this.executor<MutableList<CustomerDto?>?>("allCustomersDemo", this.client!!.allCustomers())
    }

    private fun customerByIdDemo(): Mono<Void?> {
        return this.executor<CustomerDto?>("customerByIdDemo", this.client!!.customerById(11))
    }

    private fun createCustomerDemo(): Mono<Void?> {
        return this.executor<CustomerDto?>("createCustomerDemo", this.client!!.createCustomer(CustomerDto(null, "obie", 45, "detroit")))
    }

    private fun updateCustomerDemo(): Mono<Void?> {
        return this.executor<CustomerDto?>(
            "updateCustomerDemo", this.client!!.updateCustomer(
                2,
                CustomerDto(null, "jackson", 15, "dallas")
            )
        )
    }

    private fun deleteCustomerDemo(): Mono<Void?> {
        return this.executor<DeleteResponseDto?>("deleteCustomerDemo", this.client!!.deleteCustomer(3))
    }

    private fun <T> executor(message: String?, mono: Mono<T?>): Mono<Void?> {
        return Mono.delay(Duration.ofSeconds(1))
            .doFirst(Runnable { println(message) })
            .then<T?>(mono)
            .doOnNext(Consumer { x: T? -> println(x) })
            .then()
    }
}