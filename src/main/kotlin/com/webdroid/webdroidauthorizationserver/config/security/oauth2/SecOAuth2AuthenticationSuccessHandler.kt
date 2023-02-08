package com.webdroid.webdroidauthorizationserver.config.security.oauth2

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import com.webdroid.webdroidauthorizationserver.config.security.TokenProvider
import com.webdroid.webdroidauthorizationserver.exception.BadRequestException
import com.webdroid.webdroidauthorizationserver.util.CookieUtils
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.net.URI

@Slf4j
@Component
@RequiredArgsConstructor
class SecOAuth2AuthenticationSuccessHandler @Autowired constructor(
    private val tokenProvider: TokenProvider,
    private val appProperties: AppProperties,
    private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
) : SimpleUrlAuthenticationSuccessHandler()
{

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        super.onAuthenticationSuccess(request, response, authentication)
//        val targetUrl = determineTargetUrl(request, response, authentication)
//        if (targetUrl == null) {
//            logger.debug("Target URL not found. Unable to redirect to $targetUrl")
//        }else {
//            if (response.isCommitted) {
//                logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
//                return
//            }
//            clearAuthenticationAttributes(request, response)
//            redirectStrategy.sendRedirect(request, response, targetUrl)
//        }
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String? {
        val redirectUri = CookieUtils.getCookie(
            request,
            HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME
        )
            .map { obj: Cookie -> obj.value }

        return if (redirectUri.isEmpty){
            null
        } else {
            if (!isAuthorizedRedirectUri(redirectUri.get())) {
                throw BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication")
            }
            val targetUrl = redirectUri.orElse(defaultTargetUrl)
            val token = tokenProvider.createToken(authentication)

            UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString()
        }
    }

    protected fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        return appProperties.oauth2.authorizedRedirectUris
            .stream()
            .anyMatch { authorizedRedirectUri: String ->
                // Only validate host and port. Let the clients use different paths if they want to
                val authorizedURI = URI.create(authorizedRedirectUri)
                if (authorizedURI.host.equals(clientRedirectUri.host, ignoreCase = true)
                    && authorizedURI.port == clientRedirectUri.port
                ) {
                    return@anyMatch true
                }
                false
            }
    }
}