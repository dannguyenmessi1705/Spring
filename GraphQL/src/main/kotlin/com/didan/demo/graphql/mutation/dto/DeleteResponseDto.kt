package com.didan.demo.graphql.mutation.dto

class DeleteResponseDto {
    constructor(id: Int?, status: Status?) {
        this.id = id
        this.status = status
    }

    var id: Int? = null
    var status: Status? = null
}