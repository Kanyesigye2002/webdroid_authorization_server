package com.webdroid.webdroidauthorizationserver.util

import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.util.stream.Collectors

class GenericResponse {
    var message: String
    var error: String? = null

    constructor(message: String) : super() {
        this.message = message
    }

    constructor(message: String, error: String?) : super() {
        this.message = message
        this.error = error
    }

    constructor(allErrors: List<ObjectError>, error: String?) {
        this.error = error
        val temp = allErrors.stream().map { e: ObjectError ->
            if (e is FieldError) {
                return@map "{\"field\":\"" + e.field + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}"
            } else {
                return@map "{\"object\":\"" + e.objectName + "\",\"defaultMessage\":\"" + e.defaultMessage + "\"}"
            }
        }.collect(Collectors.joining(","))
        message = "[$temp]"
    }
}