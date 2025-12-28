package com.didan.demo.graphql.client.config

import com.didan.demo.graphql.client.dto.CustomerDto
import com.didan.demo.graphql.client.dto.CustomerNotFound
import com.didan.demo.graphql.client.dto.DeleteResponseDto
import com.didan.demo.graphql.client.dto.GenericResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.graphql.client.ClientGraphQlResponse
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*
import java.util.Map
import java.util.function.Consumer
import java.util.function.Function

@Service
class CustomerClient(@Value($$"${customer.service.url}") baseUrl: String) {
    private val client: HttpGraphQlClient

    /**
     * Khởi tạo HttpGraphQlClient với WebClient cấu hình baseUrl.
     * Không dùng constructor injection để dễ dàng thay đổi baseUrl khi cần thiết.
     * init khác với constructor vì chỉ khởi tạo một lần sau khi đối tượng được tạo.
     */
    init {
        this.client = HttpGraphQlClient.builder()
            .webClient(Consumer { b: WebClient.Builder? -> b!!.baseUrl(baseUrl) })
            .build()
    }

    /**
     * Thực hiện truy vấn GraphQL thô và trả về ClientGraphQlResponse.
     * VD: rawQuery("{ customerById(id: 1) { id name email } }")
     */
    fun rawQuery(query: String): Mono<ClientGraphQlResponse?> {
        return this.client.document(query)
            .execute()
    }

    /**
     * Lấy thông tin khách hàng theo ID, trả về GenericResponse bao gồm dữ liệu hoặc lỗi.
     */
    fun getCustomerById(id: Int?): Mono<GenericResponse<CustomerDto?>?> {
        return this.client
            .documentName("customer-by-id") // Sử dụng documentName để tham chiếu đến tài liệu GraphQL đã định nghĩa sẵn (trong thư mục resources/graphql-documents)
            .variable("id", id) // Truyền biến "id" vào truy vấn
            .execute() // Thực thi truy vấn
            .map<GenericResponse<CustomerDto?>?>(Function { cr: ClientGraphQlResponse? ->
                val field = cr!!.field("customerById") // Lấy trường "customerById" từ phản hồi
                if (Objects.nonNull(field.getValue<Any?>())) GenericResponse<CustomerDto?>(field.toEntity<CustomerDto?>(CustomerDto::class.java)) else GenericResponse<CustomerDto?>(field.getErrors()) // Kiểm tra nếu có giá trị thì chuyển đổi sang CustomerDto, ngược lại trả về lỗi
            })
    }

    /**
     * Lấy thông tin khách hàng theo ID, sử dụng GraphQL union types để trả về CustomerDto hoặc CustomerNotFound.
     */
    fun getCustomerByIdWithUnion(id: Int?): Mono<Any?> {
        return this.client.documentName("customer-by-id") // Sử dụng documentName để tham chiếu đến tài liệu GraphQL đã định nghĩa sẵn (trong thư mục resources/graphql-documents)
            .variable("id", id) // Truyền biến "id" vào truy vấn
            .execute() // Thực thi truy vấn
            .map<Any?>(Function { cr: ClientGraphQlResponse? ->
                val field = cr!!.field("customerById")
                val isCustomer = "Customer" == cr.field("customerById.type").getValue<Any?>().toString()
                Objects.requireNonNull<Any?>(if (isCustomer) field.toEntity<CustomerDto?>(CustomerDto::class.java) else field.toEntity<CustomerNotFound?>(CustomerNotFound::class.java))
            })
    }

    /**
     * Thực hiện các thao tác CRUD cơ bản trên Customer thông qua GraphQL.
     */
    fun allCustomers(): Mono<MutableList<CustomerDto?>?> {
        return this.crud<MutableList<CustomerDto?>?>("GetAll", mutableMapOf<String?, Any?>(), object : ParameterizedTypeReference<MutableList<CustomerDto?>?>() {
        })
    }

    fun customerById(id: Int): Mono<CustomerDto?> {
        return this.crud<CustomerDto?>("GetCustomerById", Map.of<String?, Any?>("id", id), object : ParameterizedTypeReference<CustomerDto?>() {
        })
    }

    fun createCustomer(dto: CustomerDto): Mono<CustomerDto?> {
        return this.crud<CustomerDto?>("CreateCustomer", Map.of<String?, Any?>("customer", dto), object : ParameterizedTypeReference<CustomerDto?>() {
        })
    }

    fun updateCustomer(id: Int, dto: CustomerDto): Mono<CustomerDto?> {
        return this.crud<CustomerDto?>("UpdateCustomer", Map.of<String?, Any?>("id", id, "customer", dto), object : ParameterizedTypeReference<CustomerDto?>() {
        })
    }

    fun deleteCustomer(id: Int): Mono<DeleteResponseDto?> {
        return this.crud<DeleteResponseDto?>("DeleteCustomer", Map.of<String?, Any?>("id", id), object : ParameterizedTypeReference<DeleteResponseDto?>() {
        })
    }

    /**
     * Hàm tiện ích để thực hiện các thao tác CRUD với tên thao tác, biến và kiểu trả về động.
     */
    private fun <T> crud(operationName: String?, variables: MutableMap<String?, Any?>, type: ParameterizedTypeReference<T?>): Mono<T?> {
        return this.client.documentName("crud-operations") // Sử dụng documentName để tham chiếu đến tài liệu GraphQL đã định nghĩa sẵn (trong thư mục resources/graphql-documents)
            .operationName(operationName) // Chỉ định tên thao tác (operation) trong tài liệu GraphQL
            .variables(variables) // Truyền biến vào thao tác
            .retrieve("response") // Lấy trường "response" từ phản hồi
            .toEntity<T?>(type) // Chuyển đổi phản hồi sang kiểu động
    }
}