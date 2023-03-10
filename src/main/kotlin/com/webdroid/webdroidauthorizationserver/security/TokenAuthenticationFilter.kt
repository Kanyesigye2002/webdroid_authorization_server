package com.webdroid.webdroidauthorizationserver.security

import com.webdroid.webdroidauthorizationserver.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.*
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Slf4j
@AllArgsConstructor
class TokenAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    private val tokenProvider: TokenProvider? = null
    @Autowired
    private val userService: CustomUserDetailsService? = null
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)
            if (StringUtils.hasText(jwt) && tokenProvider!!.validateToken(jwt)) {
                val userId = tokenProvider.getUserIdFromToken(jwt)
                val userDetails = userService!!.loadUserById(userId)
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }
        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }
}