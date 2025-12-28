package com.didan.demo.graphql.validate_input_error_handling.util

import com.didan.demo.graphql.validate_input_error_handling.dto.CustomerDto
import com.didan.demo.graphql.validate_input_error_handling.entity.Customer

object EntityDtoUtil {

    fun toEntity(dto: CustomerDto): Customer {
        val customer = Customer()
        customer.name = dto.name
        customer.age = dto.age
        customer.city = dto.city
        return customer
    }

    fun toEntity(id: Int, dto: CustomerDto): Customer {
        val customer = Customer()
        customer.id = id
        customer.city = dto.city
        customer.name = dto.name
        customer.age = dto.age
        return customer
    }

    fun toDto(entity: Customer): CustomerDto {
        val dto = CustomerDto(0, "", 0, "")
        dto.id = entity.id!!
        dto.name = entity.name!!
        dto.age = entity.age!!
        dto.city = entity.city!!
        return dto
    }
}