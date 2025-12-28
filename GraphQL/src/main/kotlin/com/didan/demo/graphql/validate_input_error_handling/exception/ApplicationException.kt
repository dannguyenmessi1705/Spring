package com.didan.demo.graphql.validate_input_error_handling.exception

import org.springframework.graphql.execution.ErrorType

class ApplicationException : RuntimeException {

    var errorType: ErrorType? = null
    override var message: String? = null
    var extensions: MutableMap<String, Any>? = null

    constructor(errorType: ErrorType, message: String?, extensions: MutableMap<String, Any>) : super(message) {
        this.errorType = errorType
        this.message = message
        this.extensions = extensions
    }
}