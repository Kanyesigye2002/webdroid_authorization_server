package com.webdroid.webdroidauthorizationserver.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomAuthenticationProvider @Autowired constructor(
    private val customUserDetailsService: CustomUserDetailsService,
private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()
        val user = customUserDetailsService.loadUserByUsername(username)
        return checkPassword(user, password)
    }

    private fun checkPassword(user: UserDetails, rawPassword: String): Authentication {
        return if (passwordEncoder.matches(rawPassword, user.password)) {
            UsernamePasswordAuthenticationToken(
                user.username,
                user.password,
                user.authorities
            )
        } else {
            throw BadCredentialsException("Bad Credentials")
        }
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}