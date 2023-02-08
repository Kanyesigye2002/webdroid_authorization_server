package com.webdroid.webdroidauthorizationserver.exception

import com.webdroid.webdroidauthorizationserver.util.GenericResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailAuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @Autowired
    private val messages: MessageSource? = null
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("400 Status Code", ex)
        val result = ex.bindingResult
        val bodyOfResponse = GenericResponse(result.allErrors, "Invalid" + result.objectName)
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(InvalidOldPasswordException::class)
    fun handleInvalidOldPassword(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any>? {
        logger.error("400 Status Code", ex)
        val bodyOfResponse = GenericResponse(
            messages!!.getMessage("message.invalidOldPassword", null, request.locale),
            "InvalidOldPassword"
        )
        return handleExceptionInternal(ex!!, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    // 404
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any>? {
        logger.error("404 Status Code", ex)
        val bodyOfResponse =
            GenericResponse(messages!!.getMessage("message.userNotFound", null, request.locale), "UserNotFound")
        return handleExceptionInternal(ex!!, bodyOfResponse, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

    // 409
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExist(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any>? {
        logger.error("409 Status Code", ex)
        val bodyOfResponse =
            GenericResponse(messages!!.getMessage("message.regError", null, request.locale), "UserAlreadyExist")
        return handleExceptionInternal(ex!!, bodyOfResponse, HttpHeaders(), HttpStatus.CONFLICT, request)
    }

    // 500
    @ExceptionHandler(MailAuthenticationException::class)
    fun handleMail(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("500 Status Code", ex)
        val bodyOfResponse =
            GenericResponse(messages!!.getMessage("message.email.config.error", null, request.locale), "MailError")
        return ResponseEntity(bodyOfResponse, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleInternal(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("500 Status Code", ex)
        val bodyOfResponse =
            GenericResponse(messages!!.getMessage("message.error", null, request.locale), "InternalError")
        return ResponseEntity(bodyOfResponse, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}