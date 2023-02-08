package com.webdroid.webdroidauthorizationserver.exception

import org.springframework.security.core.AuthenticationException

class UnusualLocationException : AuthenticationException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)

    companion object {
        private const val serialVersionUID = 5861310537366287163L
    }
}