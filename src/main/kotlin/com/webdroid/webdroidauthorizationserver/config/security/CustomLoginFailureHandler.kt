package com.webdroid.webdroidauthorizationserver.config.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver
import java.io.IOException

@Component("CustomLoginFailureHandler")
class CustomLoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Autowired
    private val messages: MessageSource? = null

    @Autowired
    private val localeResolver: LocaleResolver? = null

    @Autowired
    private val request: HttpServletRequest? = null

    @Autowired
    private val loginAttemptService: LoginAttemptService? = null
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        setDefaultFailureUrl("/login?error=true")
        super.onAuthenticationFailure(request, response, exception)
        val locale = localeResolver!!.resolveLocale(request)
        var errorMessage = messages!!.getMessage("message.badCredentials", null, locale)
        if (loginAttemptService!!.isBlocked) {
            errorMessage = messages.getMessage("auth.message.blocked", null, locale)
        }
        if (exception.message
                .equals("User is disabled", ignoreCase = true)
        ) {
            errorMessage = messages.getMessage("auth.message.disabled", null, locale)
        } else if (exception.message
                .equals("User account has expired", ignoreCase = true)
        ) {
            errorMessage = messages.getMessage("auth.message.expired", null, locale)
        } else if (exception.message
                .equals("blocked", ignoreCase = true)
        ) {
            errorMessage = messages.getMessage("auth.message.blocked", null, locale)
        } else if (exception.message
                .equals("unusual location", ignoreCase = true)
        ) {
            errorMessage = messages.getMessage("auth.message.unusual.location", null, locale)
        }
        request.session
            .setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage)
    }
}