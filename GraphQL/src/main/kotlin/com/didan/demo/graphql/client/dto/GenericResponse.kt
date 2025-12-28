package com.didan.demo.graphql.client.dto

import org.springframework.graphql.ResponseError

class GenericResponse<T> {
    var data: T? = null
    var errors: List<ResponseError> = emptyList()
    var dataPresent: Boolean = false

    constructor(data: T?) {
        this.data = data
        this.errors = emptyList()
        this.dataPresent = true
    }

    constructor(errors: List<ResponseError>) {
        this.data = null
        this.errors = errors
        this.dataPresent = false
    }
}