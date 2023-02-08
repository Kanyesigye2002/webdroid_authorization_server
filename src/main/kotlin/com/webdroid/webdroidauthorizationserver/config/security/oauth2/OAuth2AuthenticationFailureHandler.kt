package com.webdroid.webdroidauthorizationserver.config.security.oauth2

import com.webdroid.webdroidauthorizationserver.util.CookieUtils
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException

@Component
class OAuth2AuthenticationFailureHandler @Autowired constructor(var httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository) : SimpleUrlAuthenticationFailureHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        var targetUrl = CookieUtils.getCookie(
            request,
            HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME
        )
            .map { obj: Cookie -> obj.value }
            .orElse("/")
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl!!)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}