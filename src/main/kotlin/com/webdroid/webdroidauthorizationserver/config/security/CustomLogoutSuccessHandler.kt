package com.webdroid.webdroidauthorizationserver.config.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component("CustomLogoutSuccessHandler")
class CustomLogoutSuccessHandler : LogoutSuccessHandler {
    @Throws(IOException::class, ServletException::class)
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val session = request.session
        session?.removeAttribute("user")
        response.sendRedirect("/logout?logSucc=true")
    }
}