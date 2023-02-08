package com.webdroid.webdroidauthorizationserver.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component("CustomLoginSuccessHandler")
class CustomLoginSuccessHandler : AuthenticationSuccessHandler {
    @Autowired
    var activeUserStore: ActiveUserStore? = null
    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val session = request.getSession(false)
        if (session != null) {
            val user = LoggedUser(authentication.name, activeUserStore!!)
            session.setAttribute("user", user)
        }
    }
}